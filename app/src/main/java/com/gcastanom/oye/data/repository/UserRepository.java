package com.gcastanom.oye.data.repository;

import android.content.Context;

import com.gcastanom.oye.data.model.PerfilRequest;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.data.model.UsuarioRequest;
import com.gcastanom.oye.data.remote.ApiService;
import com.gcastanom.oye.data.remote.RetrofitClient;

import java.util.List;

/** Mi perfil (/api/users/me), CRUD de usuarios (solo ADMIN) y reportes de usuarios. */
public class UserRepository {

    private final ApiService api;

    public UserRepository(Context context) {
        this.api = RetrofitClient.getApi(context);
    }

    // ---------- Mi perfil ----------

    public void miPerfil(Resultado<Usuario> resultado) {
        api.miPerfil().enqueue(new ApiCallback<>(resultado));
    }

    public void actualizarPerfil(PerfilRequest datos, Resultado<Usuario> resultado) {
        api.actualizarPerfil(datos).enqueue(new ApiCallback<>(resultado));
    }

    // ---------- CRUD (ADMIN) ----------

    public void listar(Resultado<List<Usuario>> resultado) {
        api.listarUsuarios().enqueue(new ApiCallback<>(resultado));
    }

    public void crear(UsuarioRequest datos, Resultado<Usuario> resultado) {
        api.crearUsuario(datos).enqueue(new ApiCallback<>(resultado));
    }

    public void actualizar(String id, UsuarioRequest datos, Resultado<Usuario> resultado) {
        api.actualizarUsuario(id, datos).enqueue(new ApiCallback<>(resultado));
    }

    public void eliminar(String id, Resultado<Void> resultado) {
        api.eliminarUsuario(id).enqueue(new ApiCallback<>(resultado));
    }

    // ---------- Reportes ----------

    /** Reporte por rol (ADMIN, REVIEWER, MEMBER) o por nombre. El filtro que va en null no se envía. */
    public void reporte(String rol, String nombre, Resultado<List<Usuario>> resultado) {
        api.reporteUsuarios(rol, nombre).enqueue(new ApiCallback<>(resultado));
    }
}
