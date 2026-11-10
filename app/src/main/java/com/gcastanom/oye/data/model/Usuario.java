package com.gcastanom.oye.data.model;

/** Un usuario tal como lo devuelve la API (nunca trae la contraseña). */
public class Usuario {
    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_REVIEWER = "REVIEWER";
    public static final String ROL_MEMBER = "MEMBER";

    private String id;
    private String username;
    private String name;
    private String email;
    private String role;
    private String status;

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getStatus() { return status; }

    public boolean esAdmin() { return ROL_ADMIN.equals(role); }
}
