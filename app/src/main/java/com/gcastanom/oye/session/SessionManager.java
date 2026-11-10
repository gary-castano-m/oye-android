package com.gcastanom.oye.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.gcastanom.oye.data.model.LoginResponse;
import com.gcastanom.oye.data.model.Usuario;
import com.google.gson.Gson;

/**
 * Guarda la sesión en el celular con SharedPreferences.
 * Cumple el mismo papel que TinyDB en Kodular, con las mismas etiquetas:
 * "sesion_token" y "sesion_usuario".
 */
public class SessionManager {

    private static final String ARCHIVO = "oye_sesion";
    private static final String CLAVE_TOKEN = "sesion_token";
    private static final String CLAVE_USUARIO = "sesion_usuario";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    /** Se llama justo después de un login exitoso. */
    public void guardarSesion(LoginResponse login) {
        prefs.edit()
                .putString(CLAVE_TOKEN, login.getAccessToken())
                .putString(CLAVE_USUARIO, gson.toJson(login.getUser()))
                .apply();
    }

    /** Se llama cuando el usuario cambia sus datos en Mi perfil. */
    public void actualizarUsuario(Usuario usuario) {
        prefs.edit().putString(CLAVE_USUARIO, gson.toJson(usuario)).apply();
    }

    public String getToken() {
        return prefs.getString(CLAVE_TOKEN, null);
    }

    public Usuario getUsuario() {
        String json = prefs.getString(CLAVE_USUARIO, null);
        return json == null ? null : gson.fromJson(json, Usuario.class);
    }

    public boolean haySesion() {
        return getToken() != null && getUsuario() != null;
    }

    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }
}
