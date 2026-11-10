package com.gcastanom.oye.data.model;

/**
 * Cuerpo de POST /api/users (crear) y PUT /api/users/{id} (editar), solo para ADMIN.
 * Al crear, status va en null y Gson no lo envía. Al editar, password en null = no cambiarla.
 */
public class UsuarioRequest {
    private final String username;
    private final String name;
    private final String email;
    private final String password;
    private final String role;
    private final String status;

    public UsuarioRequest(String username, String name, String email,
                          String password, String role, String status) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
    }
}
