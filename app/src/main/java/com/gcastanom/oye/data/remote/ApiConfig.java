package com.gcastanom.oye.data.remote;

/** Datos fijos de conexión con el backend del Corte 2. */
public final class ApiConfig {

    /** Dirección del backend en Render. Debe terminar en "/". */
    public static final String BASE_URL = "https://oye-api-j5l4.onrender.com/";

    /** Render gratuito puede tardar hasta un minuto en despertar: igual que en Kodular, 70 s. */
    public static final int TIMEOUT_SEGUNDOS = 70;

    private ApiConfig() {
    }
}
