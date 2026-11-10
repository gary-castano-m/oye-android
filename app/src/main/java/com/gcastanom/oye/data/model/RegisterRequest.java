package com.gcastanom.oye.data.model;

/** Cuerpo de POST /api/auth/register. El rol siempre queda MEMBER. */
public class RegisterRequest {
    private final String username;
    private final String name;
    private final String email;
    private final String password;

    public RegisterRequest(String username, String name, String email, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
