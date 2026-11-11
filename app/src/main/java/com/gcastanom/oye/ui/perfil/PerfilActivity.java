package com.gcastanom.oye.ui.perfil;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.PerfilRequest;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.data.repository.Resultado;
import com.gcastanom.oye.data.repository.UserRepository;
import com.gcastanom.oye.session.SessionManager;
import com.gcastanom.oye.ui.Etiquetas;
import com.gcastanom.oye.ui.Pantalla;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Mi perfil (equivale a Screen8 de Kodular).
 * Usa /api/users/me: no lleva id porque el usuario sale del token.
 * Si la contraseña se deja vacía, no se cambia.
 */
public class PerfilActivity extends AppCompatActivity {

    private static final String PATRON_USERNAME = "^[A-Za-z0-9._-]{3,30}$";

    private TextInputLayout tilUsername, tilNombre, tilEmail, tilPassword, tilConfirmar;
    private TextInputEditText etUsername, etNombre, etEmail, etPassword, etConfirmar;
    private TextView tvRol, tvEstado;
    private Button btnGuardar;
    private ProgressBar progreso;

    private UserRepository userRepository;
    private SessionManager sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Pantalla.aplicarMargenes(findViewById(R.id.main));

        userRepository = new UserRepository(this);
        sesion = new SessionManager(this);

        tilUsername = findViewById(R.id.tilUsername);
        tilNombre = findViewById(R.id.tilNombre);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmar = findViewById(R.id.tilConfirmar);
        etUsername = findViewById(R.id.etUsername);
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmar = findViewById(R.id.etConfirmar);
        tvRol = findViewById(R.id.tvRol);
        tvEstado = findViewById(R.id.tvEstado);
        btnGuardar = findViewById(R.id.btnGuardar);
        progreso = findViewById(R.id.progreso);

        btnGuardar.setOnClickListener(v -> guardar());
        cargarPerfil();
    }

    private void cargarPerfil() {
        mostrarCargando(true);
        userRepository.miPerfil(new Resultado<Usuario>() {
            @Override
            public void onExito(Usuario usuario) {
                mostrarCargando(false);
                tvEstado.setText("");
                etUsername.setText(usuario.getUsername());
                etNombre.setText(usuario.getName());
                etEmail.setText(usuario.getEmail());
                tvRol.setText(getString(R.string.menu_rol, Etiquetas.rol(PerfilActivity.this, usuario.getRole())));
            }

            @Override
            public void onError(String mensaje) {
                mostrarCargando(false);
                tvEstado.setText(mensaje);
                btnGuardar.setEnabled(false);
            }
        });
    }

    private void guardar() {
        String username = texto(etUsername).trim();
        String nombre = texto(etNombre).trim();
        String email = texto(etEmail).trim();
        String password = texto(etPassword);
        String confirmar = texto(etConfirmar);

        tilUsername.setError(null);
        tilNombre.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmar.setError(null);

        boolean ok = true;
        if (!username.matches(PATRON_USERNAME)) {
            tilUsername.setError(getString(R.string.error_username));
            ok = false;
        }
        if (nombre.length() < 3) {
            tilNombre.setError(getString(R.string.error_nombre_corto));
            ok = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_email_invalido));
            ok = false;
        }
        if (!password.isEmpty() && password.length() < 8) {
            tilPassword.setError(getString(R.string.error_password_corta));
            ok = false;
        }
        if (!password.equals(confirmar)) {
            tilConfirmar.setError(getString(R.string.error_password_no_coincide));
            ok = false;
        }
        if (!ok) {
            return;
        }

        // Contraseña vacía = null = la API no la cambia.
        PerfilRequest datos = new PerfilRequest(username, nombre, email, password.isEmpty() ? null : password);
        mostrarCargando(true);
        userRepository.actualizarPerfil(datos, new Resultado<Usuario>() {
            @Override
            public void onExito(Usuario usuario) {
                mostrarCargando(false);
                sesion.actualizarUsuario(usuario);   // el menú mostrará el nombre nuevo
                Toast.makeText(PerfilActivity.this, R.string.perfil_guardado, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String mensaje) {
                mostrarCargando(false);
                tvEstado.setText(mensaje);
            }
        });
    }

    private void mostrarCargando(boolean cargando) {
        progreso.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnGuardar.setEnabled(!cargando);
        if (cargando) {
            tvEstado.setText(R.string.conectando);
        }
    }

    private static String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString();
    }
}
