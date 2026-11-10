package com.gcastanom.oye.data.repository;

import android.content.Context;

import com.gcastanom.oye.data.model.ForgotPasswordRequest;
import com.gcastanom.oye.data.model.LoginRequest;
import com.gcastanom.oye.data.model.LoginResponse;
import com.gcastanom.oye.data.model.RegisterRequest;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.data.remote.ApiService;
import com.gcastanom.oye.data.remote.RetrofitClient;

import okhttp3.ResponseBody;

/** Peticiones públicas: despertar el servidor, login, registro y recuperar la clave. */
public class AuthRepository {

    private final ApiService api;

    public AuthRepository(Context context) {
        this.api = RetrofitClient.getApi(context);
    }

    public void despertar(Resultado<ResponseBody> resultado) {
        api.despertar().enqueue(new ApiCallback<>(resultado));
    }

    public void login(String usuario, String password, Resultado<LoginResponse> resultado) {
        api.login(new LoginRequest(usuario, password)).enqueue(new ApiCallback<>(resultado));
    }

    public void registrar(RegisterRequest datos, Resultado<Usuario> resultado) {
        api.registrar(datos).enqueue(new ApiCallback<>(resultado));
    }

    public void recuperarClave(String email, Resultado<Void> resultado) {
        api.recuperarClave(new ForgotPasswordRequest(email)).enqueue(new ApiCallback<>(resultado));
    }
}
