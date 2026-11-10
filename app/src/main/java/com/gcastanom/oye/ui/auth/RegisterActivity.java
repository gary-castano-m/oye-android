package com.gcastanom.oye.ui.auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.RegisterRequest;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.data.repository.AuthRepository;
import com.gcastanom.oye.data.repository.Resultado;
import com.gcastanom.oye.ui.Pantalla;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Registro de una cuenta nueva (equivale a Screen3 de Kodular).
 * Valida en el celular con las mismas reglas del backend, para no hacer viajes inútiles,
 * y luego envía POST /api/auth/register. La cuenta queda como MEMBER y activa.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String PATRON_USERNAME = "^[A-Za-z0-9._-]{3,30}$";

    private TextInputLayout tilUsername, tilNombre, tilEmail, tilPassword, tilConfirmar;
    private TextInputEditText etUsername, etNombre, etEmail, etPassword, etConfirmar;
    private Button btnRegistrar;
    private ProgressBar progreso;
    private TextView tvEstado;

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Pantalla.aplicarMargenes(findViewById(R.id.main));

        authRepository = new AuthRepository(this);

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
        btnRegistrar = findViewById(R.id.btnRegistrar);
        progreso = findViewById(R.id.progreso);
        tvEstado = findViewById(R.id.tvEstado);

        btnRegistrar.setOnClickListener(v -> registrar());
        findViewById(R.id.btnYaTengoCuenta).setOnClickListener(v -> finish());
    }

    private void registrar() {
        String username = texto(etUsername).trim();
        String nombre = texto(etNombre).trim();
        String email = texto(etEmail).trim();
        String password = texto(etPassword);
        String confirmar = texto(etConfirmar);

        limpiarErrores();
        if (!datosValidos(username, nombre, email, password, confirmar)) {
            return;
        }

        mostrarCargando(true);
        authRepository.registrar(new RegisterRequest(username, nombre, email, password),
                new Resultado<Usuario>() {
                    @Override
                    public void onExito(Usuario usuario) {
                        mostrarCargando(false);
                        Toast.makeText(RegisterActivity.this,
                                R.string.registro_exitoso, Toast.LENGTH_LONG).show();
                        finish();   // vuelve al login, que quedó debajo
                    }

                    @Override
                    public void onError(String mensaje) {
                        mostrarCargando(false);
                        tvEstado.setText(mensaje);   // por ejemplo, 409: usuario o correo repetido
                    }
                });
    }

    private boolean datosValidos(String username, String nombre, String email,
                                 String password, String confirmar) {
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
        if (password.length() < 8) {
            tilPassword.setError(getString(R.string.error_password_corta));
            ok = false;
        }
        if (!password.equals(confirmar)) {
            tilConfirmar.setError(getString(R.string.error_password_no_coincide));
            ok = false;
        }
        return ok;
    }

    private void limpiarErrores() {
        tilUsername.setError(null);
        tilNombre.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmar.setError(null);
        tvEstado.setText("");
    }

    private void mostrarCargando(boolean cargando) {
        progreso.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnRegistrar.setEnabled(!cargando);
        if (cargando) {
            tvEstado.setText(R.string.conectando);
        }
    }

    private static String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString();
    }
}
