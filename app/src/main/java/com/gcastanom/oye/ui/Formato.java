package com.gcastanom.oye.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Formatos para mostrar dinero y fechas como se leen en Colombia. */
public final class Formato {

    private static final DecimalFormatSymbols SIMBOLOS = new DecimalFormatSymbols(Locale.forLanguageTag("es-CO"));

    static {
        SIMBOLOS.setGroupingSeparator('.');
        SIMBOLOS.setDecimalSeparator(',');
    }

    private Formato() {
    }

    /** 11900 → "$ 11.900"   ·   1234.5 → "$ 1.234,5" */
    public static String dinero(BigDecimal valor) {
        if (valor == null) {
            return "$ 0";
        }
        return new DecimalFormat("$ #,##0.##", SIMBOLOS).format(valor);
    }

    /** "2026-11-03" (como lo envía la API) → "03/11/2026" */
    public static String fecha(String iso) {
        if (iso == null || iso.length() < 10) {
            return "";
        }
        return iso.substring(8, 10) + "/" + iso.substring(5, 7) + "/" + iso.substring(0, 4);
    }
}
