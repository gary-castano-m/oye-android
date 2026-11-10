package com.gcastanom.oye.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.LoginResponse;
import com.gcastanom.oye.data.repository.AuthRepository;
import com.gcastanom.oye.data.repository.Resultado;
import com.gcastanom.oye.session.SessionManager;
import com.gcastanom.oye.ui.Navegacion;
import com.gcastanom.oye.ui.Pantalla;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Inicio de sesión (equivale a Screen2 de Kodular).
 * Acepta username o correo, guarda el token y abre el menú.
 * También tiene el diálogo para recuperar la contraseña por correo.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int LARGO_MINIMO_PASSWORD = 8;

    private TextInputLayout tilUsuario;
    private TextInputLayout tilPassword;
    private TextInputEditText etUsuario;
    private TextInputEditText etPassword;
    private Button btnEntrar;
    private ProgressBar progreso;
    private TextView tvEstado;

    private AuthRepository authRepository;
    private SessionManager sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Pantalla.aplicarMargenes(findViewById(R.id.main));

        authRepository = new AuthRepository(this);
        sesion = new SessionManager(this);

        tilUsuario = findViewById(R.id.tilUsuario);
        tilPassword = findViewById(R.id.tilPassword);
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnEntrar = findViewById(R.id.btnEntrar);
        progreso = findViewById(R.id.progreso);
        tvEstado = findViewById(R.id.tvEstado);

        btnEntrar.setOnClickListener(v -> iniciarSesion());
        findViewById(R.id.btnOlvide).setOnClickListener(v -> mostrarDialogoRecuperar());
        findViewById(R.id.btnCrearCuenta).setOnClickListener(
                v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void iniciarSesion() {
        String usuario = texto(etUsuario).trim();
        String password = texto(etPassword);   // la contraseña no se recorta

        tilUsuario.setError(null);
        tilPassword.setError(null);
        tvEstado.setText("");

        if (usuario.isEmpty()) {
            tilUsuario.setError(getString(R.string.error_campo_obligatorio));
            return;
        }
        if (password.length() < LARGO_MINIMO_PASSWORD) {
            tilPassword.setError(getString(R.string.error_password_corta));
            return;
        }

        mostrarCargando(true);
        authRepository.login(usuario, password, new Resultado<LoginResponse>() {
            @Override
            public void onExito(LoginResponse login) {
                mostrarCargando(false);
                sesion.guardarSesion(login);
                Toast.makeText(LoginActivity.this,
                        getString(R.string.login_bienvenida, login.getUser().getName()),
                        Toast.LENGTH_SHORT).show();
                Navegacion.irAlMenu(LoginActivity.this);
            }

            @Override
            public void onError(String mensaje) {
                mostrarCargando(false);
                tvEstado.setText(mensaje);
            }
        });
    }

    private void mostrarDialogoRecuperar() {
        View vista = getLayoutInflater().inflate(R.layout.dialog_recuperar, null);
        TextInputLayout tilEmail = vista.findViewById(R.id.tilEmailRecuperar);
        TextInputEditText etEmail = vista.findViewById(R.id.etEmailRecuperar);

        AlertDialog dialogo = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.recuperar_titulo)
                .setMessage(R.string.recuperar_mensaje)
                .setView(vista)
                .setPositiveButton(R.string.recuperar_enviar, null)
                .setNegativeButton(R.string.cancelar, null)
                .create();
        dialogo.show();

        // Se reemplaza el clic de "Enviar" para que el diálogo no se cierre si el correo es inválido.
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String email = texto(etEmail).trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.setError(getString(R.string.error_email_invalido));
                return;
            }
            dialogo.dismiss();
            enviarRecuperacion(email);
        });
    }

    private void enviarRecuperacion(String email) {
        mostrarCargando(true);
        authRepository.recuperarClave(email, new Resultado<Void>() {
            @Override
            public void onExito(Void datos) {
                mostrarCargando(false);
                // La API responde 204 exista o no el correo, para no revelar quién está registrado.
                tvEstado.setText(R.string.recuperar_enviado);
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
        btnEntrar.setEnabled(!cargando);
        if (cargando) {
            tvEstado.setText(R.string.conectando);
        }
    }

    private static String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString();
    }
}
