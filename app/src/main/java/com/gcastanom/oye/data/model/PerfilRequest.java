package com.gcastanom.oye.data.model;

/** Cuerpo de PUT /api/users/me. Si password es null, la contraseña no cambia. */
public class PerfilRequest {
    private final String username;
    private final String name;
    private final String email;
    private final String password;

    public PerfilRequest(String username, String name, String email, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
