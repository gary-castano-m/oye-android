package com.gcastanom.oye.data.model;

import java.math.BigDecimal;
import java.util.List;

/** Respuesta de GET /api/gastos/reports: la lista filtrada y sus totales. */
public class ReporteGastos {
    private int cantidad;
    private BigDecimal totalSinIva;
    private BigDecimal totalIva;
    private BigDecimal totalConIva;
    private List<Gasto> gastos;

    public int getCantidad() { return cantidad; }
    public BigDecimal getTotalSinIva() { return totalSinIva; }
    public BigDecimal getTotalIva() { return totalIva; }
    public BigDecimal getTotalConIva() { return totalConIva; }
    public List<Gasto> getGastos() { return gastos; }
}
