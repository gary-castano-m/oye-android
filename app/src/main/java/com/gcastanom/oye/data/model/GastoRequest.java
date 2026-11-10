package com.gcastanom.oye.data.model;

import java.math.BigDecimal;

/**
 * Cuerpo de POST /api/gastos y PUT /api/gastos/{id}.
 * Solo cuatro datos: el IVA y el total los calcula la API.
 */
public class GastoRequest {
    private final String fecha;            // formato yyyy-MM-dd
    private final BigDecimal valorTotalSinIva;
    private final String lugar;
    private final String descripcion;

    public GastoRequest(String fecha, BigDecimal valorTotalSinIva, String lugar, String descripcion) {
        this.fecha = fecha;
        this.valorTotalSinIva = valorTotalSinIva;
        this.lugar = lugar;
        this.descripcion = descripcion;
    }
}
