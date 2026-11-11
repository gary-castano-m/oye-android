package com.gcastanom.oye.ui;

import android.content.Context;

import com.gcastanom.oye.R;

/**
 * La API usa códigos en inglés (ADMIN, ACTIVE...). Aquí se traducen para mostrarlos,
 * y se guardan en el mismo orden en que aparecen en las listas desplegables.
 */
public final class Etiquetas {

    public static final String[] ROLES = {"MEMBER", "REVIEWER", "ADMIN"};
    public static final String[] ESTADOS = {"ACTIVE", "INACTIVE", "PENDING", "BLOCKED"};

    private Etiquetas() {
    }

    public static String[] nombresDeRoles(Context c) {
        return new String[]{c.getString(R.string.rol_member), c.getString(R.string.rol_reviewer),
                c.getString(R.string.rol_admin)};
    }

    public static String[] nombresDeEstados(Context c) {
        return new String[]{c.getString(R.string.estado_activo), c.getString(R.string.estado_inactivo),
                c.getString(R.string.estado_pendiente), c.getString(R.string.estado_bloqueado)};
    }

    public static String rol(Context c, String codigo) {
        return nombresDeRoles(c)[posicion(ROLES, codigo)];
    }

    public static String estado(Context c, String codigo) {
        return nombresDeEstados(c)[posicion(ESTADOS, codigo)];
    }

    /** Posición del código en la lista; si no está, la primera. */
    public static int posicion(String[] codigos, String codigo) {
        for (int i = 0; i < codigos.length; i++) {
            if (codigos[i].equals(codigo)) {
                return i;
            }
        }
        return 0;
    }
}
