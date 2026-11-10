package com.gcastanom.oye.ui;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Desde Android 15 las apps se dibujan detrás de la barra de estado y de navegación.
 * Este método agrega el margen justo para que nada quede tapado, ni siquiera por el teclado.
 */
public final class Pantalla {

    private Pantalla() {
    }

    public static void aplicarMargenes(View raiz) {
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (vista, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });
    }
}
