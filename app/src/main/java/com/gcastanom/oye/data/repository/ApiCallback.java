package com.gcastanom.oye.data.repository;

import com.gcastanom.oye.data.model.ApiError;
import com.google.gson.Gson;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Traduce la respuesta de Retrofit a un Resultado.
 * Revisa el código HTTP (como el GotText de Kodular) y convierte
 * los errores en mensajes en español para la pantalla.
 */
class ApiCallback<T> implements Callback<T> {

    private static final Gson GSON = new Gson();

    private final Resultado<T> resultado;

    ApiCallback(Resultado<T> resultado) {
        this.resultado = resultado;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            resultado.onExito(response.body());
        } else {
            resultado.onError(mensajeDeError(response));
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t instanceof SocketTimeoutException) {
            resultado.onError("El servidor tardó demasiado en responder. Intenta de nuevo.");
        } else {
            resultado.onError("No se pudo conectar con el servidor. Revisa tu conexión a internet.");
        }
    }

    private static String mensajeDeError(Response<?> response) {
        String mensajeApi = leerMensajeDeLaApi(response);
        switch (response.code()) {
            case 400:
                return mensajeApi != null ? mensajeApi : "Hay datos inválidos.";
            case 401:
                return mensajeApi != null ? mensajeApi : "Tu sesión no es válida. Vuelve a iniciar sesión.";
            case 403:
                return "No tienes permiso para hacer esto.";
            case 404:
                return "No se encontró lo que buscas.";
            case 409:
                return mensajeApi != null ? mensajeApi : "Ese dato ya existe.";
            default:
                if (response.code() >= 500) {
                    return "El servidor tuvo un problema. Intenta más tarde.";
                }
                return "Error " + response.code();
        }
    }

    /** Lee {"status":..., "message":"..."} del cuerpo de error, si viene. */
    private static String leerMensajeDeLaApi(Response<?> response) {
        try {
            if (response.errorBody() == null) {
                return null;
            }
            ApiError error = GSON.fromJson(response.errorBody().string(), ApiError.class);
            if (error == null || error.getMessage() == null || error.getMessage().trim().isEmpty()) {
                return null;
            }
            return error.getMessage();
        } catch (Exception e) {
            return null;
        }
    }
}
