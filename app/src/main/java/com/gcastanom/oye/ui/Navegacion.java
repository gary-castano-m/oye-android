package com.gcastanom.oye.ui;

import android.app.Activity;
import android.content.Intent;

import com.gcastanom.oye.ui.auth.LoginActivity;
import com.gcastanom.oye.ui.menu.MenuActivity;

/**
 * Cambios de pantalla que "reinician" la app: al entrar o al cerrar sesión
 * se borra el historial, para que el botón Atrás no regrese a una pantalla vieja.
 */
public final class Navegacion {

    private Navegacion() {
    }

    public static void irAlMenu(Activity desde) {
        abrirLimpiando(desde, new Intent(desde, MenuActivity.class));
    }

    public static void irAlLogin(Activity desde) {
        abrirLimpiando(desde, new Intent(desde, LoginActivity.class));
    }

    private static void abrirLimpiando(Activity desde, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        desde.startActivity(intent);
        desde.finish();
    }
}
