package com.gcastanom.oye.data.model;

import java.math.BigDecimal;

/** Un gasto tal como lo devuelve la API: los campos del ejercicio 6. */
public class Gasto {
    private String id;
    private String fecha;
    private BigDecimal valorTotalSinIva;
    private BigDecimal ivaTotal;
    private BigDecimal valorTotalConIva;
    private String usuarioId;
    private String nombreUsuario;
    private String lugar;
    private String descripcion;

    public String getId() { return id; }
    public String getFecha() { return fecha; }
    public BigDecimal getValorTotalSinIva() { return valorTotalSinIva; }
    public BigDecimal getIvaTotal() { return ivaTotal; }
    public BigDecimal getValorTotalConIva() { return valorTotalConIva; }
    public String getUsuarioId() { return usuarioId; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getLugar() { return lugar; }
    public String getDescripcion() { return descripcion; }
}
