package com.gcastanom.oye.ui.menu;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.session.SessionManager;
import com.gcastanom.oye.ui.Navegacion;
import com.gcastanom.oye.ui.Pantalla;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Menú principal. Une los dos menús de Kodular (Screen4 y Screen5):
 * todos ven la sección de gastos y solo el ADMIN ve la sección de administración.
 */
public class MenuActivity extends AppCompatActivity {

    private SessionManager sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Pantalla.aplicarMargenes(findViewById(R.id.main));

        sesion = new SessionManager(this);
        if (!sesion.haySesion()) {
            Navegacion.irAlLogin(this);
            return;
        }

        configurarBotones();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Se repinta al volver, por ejemplo después de editar el nombre en Mi perfil.
        Usuario usuario = sesion.getUsuario();
        if (usuario != null) {
            mostrarUsuario(usuario);
        }
    }

    private void mostrarUsuario(Usuario usuario) {
        TextView tvSaludo = findViewById(R.id.tvSaludo);
        TextView tvRol = findViewById(R.id.tvRol);
        tvSaludo.setText(getString(R.string.menu_saludo, usuario.getName()));
        tvRol.setText(getString(R.string.menu_rol, nombreDelRol(usuario.getRole())));
        findViewById(R.id.seccionAdmin).setVisibility(usuario.esAdmin() ? View.VISIBLE : View.GONE);
    }

    private void configurarBotones() {
        // Estas opciones se conectan en las siguientes fases.
        findViewById(R.id.btnRegistrarGasto).setOnClickListener(v -> proximamente());
        findViewById(R.id.btnMisGastos).setOnClickListener(v -> proximamente());
        findViewById(R.id.btnReporteGastos).setOnClickListener(v -> proximamente());
        findViewById(R.id.btnPerfil).setOnClickListener(v -> proximamente());
        findViewById(R.id.btnUsuarios).setOnClickListener(v -> proximamente());
        findViewById(R.id.btnReporteUsuarios).setOnClickListener(v -> proximamente());

        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> confirmarCierreDeSesion());
    }

    private void confirmarCierreDeSesion() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.menu_cerrar_sesion)
                .setMessage(R.string.menu_cerrar_sesion_pregunta)
                .setPositiveButton(R.string.menu_cerrar_sesion, (d, w) -> {
                    sesion.cerrarSesion();
                    Navegacion.irAlLogin(this);
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void proximamente() {
        Toast.makeText(this, R.string.proximamente, Toast.LENGTH_SHORT).show();
    }

    private String nombreDelRol(String rol) {
        if (Usuario.ROL_ADMIN.equals(rol)) {
            return getString(R.string.rol_admin);
        }
        if (Usuario.ROL_REVIEWER.equals(rol)) {
            return getString(R.string.rol_reviewer);
        }
        return getString(R.string.rol_member);
    }
}
