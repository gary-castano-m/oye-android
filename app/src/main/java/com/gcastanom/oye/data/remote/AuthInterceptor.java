package com.gcastanom.oye.data.remote;

import com.gcastanom.oye.session.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Agrega "Authorization: Bearer <token>" a cada petición privada.
 * Es el equivalente del procedimiento "encabezados" de Kodular,
 * pero se escribe una sola vez para toda la app.
 */
public class AuthInterceptor implements Interceptor {

    private final SessionManager sesion;

    public AuthInterceptor(SessionManager sesion) {
        this.sesion = sesion;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = sesion.getToken();
        boolean esPublica = original.url().encodedPath().startsWith("/api/auth/");

        if (token == null || esPublica) {
            return chain.proceed(original);
        }

        Request conToken = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(conToken);
    }
}
