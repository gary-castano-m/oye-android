package com.gcastanom.oye.data.model;

/** Cuerpo de POST /api/auth/login. "usuario" acepta el username o el correo. */
public class LoginRequest {
    private final String usuario;
    private final String password;

    public LoginRequest(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }
}
