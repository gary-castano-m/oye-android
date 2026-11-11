package com.gcastanom.oye.ui.usuarios;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.data.model.UsuarioRequest;
import com.gcastanom.oye.data.repository.Resultado;
import com.gcastanom.oye.data.repository.UserRepository;
import com.gcastanom.oye.session.SessionManager;
import com.gcastanom.oye.ui.Etiquetas;
import com.gcastanom.oye.ui.Pantalla;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

/**
 * Crear o editar un usuario (solo ADMIN).
 * - Sin extra: POST /api/users. La contraseña es obligatoria y el usuario queda activo.
 * - Con EXTRA_USUARIO_JSON: PUT /api/users/{id}. Contraseña vacía = no se cambia.
 * El ADMIN no puede cambiar su propio rol ni su estado, para no quedarse sin acceso.
 */
public class UsuarioFormActivity extends AppCompatActivity {

    public static final String EXTRA_USUARIO_JSON = "usuario_json";

    private static final String PATRON_USERNAME = "^[A-Za-z0-9._-]{3,30}$";

    private TextInputLayout tilUsername, tilNombre, tilEmail, tilPassword;
    private TextInputEditText etUsername, etNombre, etEmail, etPassword;
    private Spinner spRol, spEstado;
    private TextView tvEstado;
    private Button btnGuardar;
    private ProgressBar progreso;

    private UserRepository userRepository;
    private SessionManager sesion;
    private Usuario editando;   // null = usuario nuevo
    private boolean esMiUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_form);
        Pantalla.aplicarMargenes(findViewById(R.id.main));

        userRepository = new UserRepository(this);
        sesion = new SessionManager(this);

        tilUsername = findViewById(R.id.tilUsername);
        tilNombre = findViewById(R.id.tilNombre);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spRol = findViewById(R.id.spRol);
        spEstado = findViewById(R.id.spEstado);
        tvEstado = findViewById(R.id.tvEstado);
        btnGuardar = findViewById(R.id.btnGuardar);
        progreso = findViewById(R.id.progreso);

        spRol.setAdapter(listaDesplegable(Etiquetas.nombresDeRoles(this)));
        spEstado.setAdapter(listaDesplegable(Etiquetas.nombresDeEstados(this)));

        String json = getIntent().getStringExtra(EXTRA_USUARIO_JSON);
        editando = json == null ? null : new Gson().fromJson(json, Usuario.class);

        TextView tvTitulo = findViewById(R.id.tvTitulo);
        if (editando == null) {
            tvTitulo.setText(R.string.usuario_titulo_nuevo);
            tilPassword.setHint(R.string.registro_password);
            findViewById(R.id.grupoEstado).setVisibility(View.GONE);   // al crear, la API lo deja activo
        } else {
            tvTitulo.setText(R.string.usuario_titulo_editar);
            tilPassword.setHint(R.string.usuario_password_opcional);
            llenar(editando);
        }
        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void llenar(Usuario u) {
        etUsername.setText(u.getUsername());
        etNombre.setText(u.getName());
        etEmail.setText(u.getEmail());
        spRol.setSelection(Etiquetas.posicion(Etiquetas.ROLES, u.getRole()));
        spEstado.setSelection(Etiquetas.posicion(Etiquetas.ESTADOS, u.getStatus()));

        Usuario yo = sesion.getUsuario();
        esMiUsuario = yo != null && yo.getId().equals(u.getId());
        if (esMiUsuario) {
            spRol.setEnabled(false);
            spEstado.setEnabled(false);
            tvEstado.setText(R.string.usuario_es_tu_cuenta);
        }
    }

    private void guardar() {
        String username = texto(etUsername).trim();
        String nombre = texto(etNombre).trim();
        String email = texto(etEmail).trim();
        String password = texto(etPassword);
        String rol = Etiquetas.ROLES[spRol.getSelectedItemPosition()];
        String estado = Etiquetas.ESTADOS[spEstado.getSelectedItemPosition()];

        tilUsername.setError(null);
        tilNombre.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);

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
        boolean passwordObligatoria = editando == null;
        if ((passwordObligatoria || !password.isEmpty()) && password.length() < 8) {
            tilPassword.setError(getString(R.string.error_password_corta));
            ok = false;
        }
        if (!ok) {
            return;
        }

        Resultado<Usuario> alTerminar = new Resultado<Usuario>() {
            @Override
            public void onExito(Usuario usuario) {
                mostrarCargando(false);
                if (esMiUsuario) {
                    sesion.actualizarUsuario(usuario);
                }
                Toast.makeText(UsuarioFormActivity.this, R.string.usuario_guardado, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String mensaje) {
                mostrarCargando(false);
                tvEstado.setText(mensaje);
            }
        };

        mostrarCargando(true);
        if (editando == null) {
            userRepository.crear(new UsuarioRequest(username, nombre, email, password, rol, null), alTerminar);
        } else {
            userRepository.actualizar(editando.getId(), new UsuarioRequest(username, nombre, email,
                    password.isEmpty() ? null : password, rol, estado), alTerminar);
        }
    }

    private ArrayAdapter<String> listaDesplegable(String[] opciones) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
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
