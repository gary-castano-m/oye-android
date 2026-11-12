package com.gcastanom.oye.ui;

import android.app.Activity;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Desde Android 15 las apps se dibujan detrás de la barra de estado y de navegación.
 * Estos métodos agregan el margen justo para que nada quede tapado, ni siquiera por el teclado.
 */
public final class Pantalla {

    private Pantalla() {
    }

    /** Pantallas normales: todo el contenido se aleja de las barras del sistema. */
    public static void aplicarMargenes(View raiz) {
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (vista, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });
    }

    /**
     * Pantallas con encabezado azul (login y menú): el azul sube hasta detrás de la barra
     * de estado, y solo el contenido de abajo se aleja de la barra de navegación y del teclado.
     */
    public static void aplicarMargenesConEncabezado(View raiz, View encabezado, View contenido) {
        final int eIzq = encabezado.getPaddingLeft(), eArr = encabezado.getPaddingTop(),
                eDer = encabezado.getPaddingRight(), eAba = encabezado.getPaddingBottom();
        final int cIzq = contenido.getPaddingLeft(), cArr = contenido.getPaddingTop(),
                cDer = contenido.getPaddingRight(), cAba = contenido.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(raiz, (vista, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            encabezado.setPadding(eIzq + barras.left, eArr + barras.top, eDer + barras.right, eAba);
            contenido.setPadding(cIzq + barras.left, cArr, cDer + barras.right, cAba + barras.bottom);
            return insets;
        });
    }

    /** Íconos blancos en la barra de estado (hora, batería), para fondos azules. */
    public static void iconosClaros(Activity activity) {
        WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
    }
}
