package com.gcastanom.oye.ui;

import android.app.DatePickerDialog;
import android.content.Context;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

/** Fechas en el formato que usa la API (yyyy-MM-dd) y el calendario para elegirlas. */
public final class Fechas {

    private Fechas() {
    }

    public static String hoy() {
        Calendar c = Calendar.getInstance();
        return iso(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    public static String primerDiaDelMes() {
        Calendar c = Calendar.getInstance();
        return iso(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, 1);
    }

    /** Abre el calendario con la fecha que tenga el campo y escribe ahí la elegida. */
    public static void elegir(Context context, TextInputEditText campo) {
        Calendar c = Calendar.getInstance();
        String actual = campo.getText() == null ? "" : campo.getText().toString();
        if (actual.length() == 10) {
            c.set(Integer.parseInt(actual.substring(0, 4)),
                    Integer.parseInt(actual.substring(5, 7)) - 1,
                    Integer.parseInt(actual.substring(8, 10)));
        }
        new DatePickerDialog(context,
                (vista, anio, mes, dia) -> campo.setText(iso(anio, mes + 1, dia)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private static String iso(int anio, int mes, int dia) {
        return String.format(Locale.US, "%04d-%02d-%02d", anio, mes, dia);
    }
}
