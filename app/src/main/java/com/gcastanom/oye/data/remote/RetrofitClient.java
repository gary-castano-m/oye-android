package com.gcastanom.oye.data.remote;

import android.content.Context;

import com.gcastanom.oye.session.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Arma Retrofit una sola vez y lo reutiliza en toda la app (patrón Singleton).
 * Aquí se juntan: la dirección del backend, el interceptor del token,
 * los tiempos de espera y Gson para convertir JSON.
 */
public final class RetrofitClient {

    private static ApiService api;

    private RetrofitClient() {
    }

    public static synchronized ApiService getApi(Context context) {
        if (api == null) {
            SessionManager sesion = new SessionManager(context.getApplicationContext());

            // BASIC muestra en Logcat solo método, ruta, código y tiempo; nunca el token.
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BASIC);

            OkHttpClient cliente = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(sesion))
                    .addInterceptor(log)
                    .connectTimeout(ApiConfig.TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
                    .readTimeout(ApiConfig.TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
                    .writeTimeout(ApiConfig.TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
                    .build();

            api = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .client(cliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
        }
        return api;
    }
}
