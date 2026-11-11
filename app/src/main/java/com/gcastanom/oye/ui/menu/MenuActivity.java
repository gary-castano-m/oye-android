package com.gcastanom.oye.ui.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.session.SessionManager;
import com.gcastanom.oye.ui.Etiquetas;
import com.gcastanom.oye.ui.Navegacion;
import com.gcastanom.oye.ui.Pantalla;
import com.gcastanom.oye.ui.gastos.GastoFormActivity;
import com.gcastanom.oye.ui.gastos.GastosListActivity;
import com.gcastanom.oye.ui.perfil.PerfilActivity;
import com.gcastanom.oye.ui.reportes.ReporteGastosActivity;
import com.gcastanom.oye.ui.reportes.ReporteUsuariosActivity;
import com.gcastanom.oye.ui.usuarios.UsuariosActivity;
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

        abrirAlTocar(R.id.btnRegistrarGasto, GastoFormActivity.class);
        abrirAlTocar(R.id.btnMisGastos, GastosListActivity.class);
        abrirAlTocar(R.id.btnReporteGastos, ReporteGastosActivity.class);
        abrirAlTocar(R.id.btnPerfil, PerfilActivity.class);
        abrirAlTocar(R.id.btnUsuarios, UsuariosActivity.class);
        abrirAlTocar(R.id.btnReporteUsuarios, ReporteUsuariosActivity.class);

        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> confirmarCierreDeSesion());
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
        tvRol.setText(getString(R.string.menu_rol, Etiquetas.rol(this, usuario.getRole())));
        findViewById(R.id.seccionAdmin).setVisibility(usuario.esAdmin() ? View.VISIBLE : View.GONE);
    }

    private void abrirAlTocar(int idBoton, Class<? extends Activity> pantalla) {
        findViewById(idBoton).setOnClickListener(v -> startActivity(new Intent(this, pantalla)));
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
}
