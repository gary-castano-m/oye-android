package com.gcastanom.oye.data.model;

/** Cuerpo de POST /api/auth/forgot-password. */
public class ForgotPasswordRequest {
    private final String email;

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }
}
