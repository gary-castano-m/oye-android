package com.gcastanom.oye.data.model;

/** Respuesta del login: el token JWT y los datos del usuario que entró. */
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private Usuario user;

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public Usuario getUser() { return user; }
}
