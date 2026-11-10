package com.gcastanom.oye.data.model;

/** Cuerpo de error que devuelve la API: {"status": 409, "message": "..."}. */
public class ApiError {
    private int status;
    private String message;

    public int getStatus() { return status; }
    public String getMessage() { return message; }
}
