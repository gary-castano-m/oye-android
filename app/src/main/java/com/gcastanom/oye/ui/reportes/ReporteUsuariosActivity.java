package com.gcastanom.oye.ui.reportes;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.data.repository.Resultado;
import com.gcastanom.oye.data.repository.UserRepository;
import com.gcastanom.oye.ui.Etiquetas;
import com.gcastanom.oye.ui.Pantalla;
import com.gcastanom.oye.ui.usuarios.UsuarioAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

/**
 * Los dos reportes parametrizados de la tabla Usuario (Screen12 y Screen13 de Kodular, unidos):
 * - Por rol:    GET /api/users/reports?role=ADMIN
 * - Por nombre: GET /api/users/reports?name=gary
 */
public class ReporteUsuariosActivity extends AppCompatActivity {

    private RadioGroup rgTipo;
    private View grupoRol;
    private TextInputLayout tilNombre;
    private TextInputEditText etNombre;
    private Spinner spRol;
    private TextView tvResultado;
    private Button btnGenerar;
    private ProgressBar progreso;
    private UsuarioAdapter adapter;

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_usuarios);
        Pantalla.aplicarMargenes(findViewById(R.id.main));

        userRepository = new UserRepository(this);

        rgTipo = findViewById(R.id.rgTipo);
        grupoRol = findViewById(R.id.grupoRol);
        tilNombre = findViewById(R.id.tilNombre);
        etNombre = findViewById(R.id.etNombre);
        spRol = findViewById(R.id.spRol);
        tvResultado = findViewById(R.id.tvResultado);
        btnGenerar = findViewById(R.id.btnGenerar);
        progreso = findViewById(R.id.progreso);

        ArrayAdapter<String> roles = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Etiquetas.nombresDeRoles(this));
        roles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRol.setAdapter(roles);

        adapter = new UsuarioAdapter(null);   // en el reporte las filas solo se leen
        RecyclerView rv = findViewById(R.id.rvUsuarios);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        rgTipo.setOnCheckedChangeListener((grupo, id) -> mostrarFiltro());
        btnGenerar.setOnClickListener(v -> generar());
        mostrarFiltro();
    }

    private boolean porRol() {
        return rgTipo.getCheckedRadioButtonId() == R.id.rbRol;
    }

    private void mostrarFiltro() {
        grupoRol.setVisibility(porRol() ? View.VISIBLE : View.GONE);
        tilNombre.setVisibility(porRol() ? View.GONE : View.VISIBLE);
    }

    private void generar() {
        tilNombre.setError(null);
        String rol = null, nombre = null;
        if (porRol()) {
            rol = Etiquetas.ROLES[spRol.getSelectedItemPosition()];
        } else {
            nombre = etNombre.getText() == null ? "" : etNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                tilNombre.setError(getString(R.string.error_campo_obligatorio));
                return;
            }
        }

        progreso.setVisibility(View.VISIBLE);
        btnGenerar.setEnabled(false);
        tvResultado.setText(R.string.conectando);
        userRepository.reporte(rol, nombre, new Resultado<List<Usuario>>() {
            @Override
            public void onExito(List<Usuario> usuarios) {
                terminar();
                tvResultado.setText(getString(R.string.reporte_usuarios_cantidad, usuarios.size()));
                adapter.setUsuarios(usuarios);
            }

            @Override
            public void onError(String mensaje) {
                terminar();
                tvResultado.setText(mensaje);
            }
        });
    }

    private void terminar() {
        progreso.setVisibility(View.GONE);
        btnGenerar.setEnabled(true);
    }
}
