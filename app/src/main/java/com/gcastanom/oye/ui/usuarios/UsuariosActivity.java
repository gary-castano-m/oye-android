package com.gcastanom.oye.ui.usuarios;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.data.repository.Resultado;
import com.gcastanom.oye.data.repository.UserRepository;
import com.gcastanom.oye.session.SessionManager;
import com.gcastanom.oye.ui.Pantalla;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.util.List;

/**
 * CRUD de la tabla Usuario, solo para ADMIN (equivale a Screen11 de Kodular).
 * Lista con GET /api/users; al tocar un usuario: editar o eliminar (DELETE /api/users/{id}).
 */
public class UsuariosActivity extends AppCompatActivity {

    private final Gson gson = new Gson();

    private UserRepository userRepository;
    private UsuarioAdapter adapter;
    private ProgressBar progreso;
    private TextView tvResumen;
    private String miId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);
        Pantalla.aplicarMargenes(findViewById(R.id.main));

        userRepository = new UserRepository(this);
        progreso = findViewById(R.id.progreso);
        tvResumen = findViewById(R.id.tvResumen);
        Usuario yo = new SessionManager(this).getUsuario();
        miId = yo == null ? null : yo.getId();

        adapter = new UsuarioAdapter(this::mostrarOpciones);
        RecyclerView rv = findViewById(R.id.rvUsuarios);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        findViewById(R.id.btnNuevoUsuario).setOnClickListener(
                v -> startActivity(new Intent(this, UsuarioFormActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        progreso.setVisibility(View.VISIBLE);
        userRepository.listar(new Resultado<List<Usuario>>() {
            @Override
            public void onExito(List<Usuario> usuarios) {
                progreso.setVisibility(View.GONE);
                adapter.setUsuarios(usuarios);
                tvResumen.setText(getString(R.string.usuarios_resumen, usuarios.size()));
            }

            @Override
            public void onError(String mensaje) {
                progreso.setVisibility(View.GONE);
                tvResumen.setText(mensaje);
            }
        });
    }

    private void mostrarOpciones(Usuario usuario) {
        String[] opciones = {getString(R.string.gastos_editar), getString(R.string.gastos_eliminar)};
        new MaterialAlertDialogBuilder(this)
                .setTitle(usuario.getName())
                .setItems(opciones, (d, cual) -> {
                    if (cual == 0) {
                        // El usuario completo viaja a la otra pantalla como texto JSON.
                        Intent intent = new Intent(this, UsuarioFormActivity.class);
                        intent.putExtra(UsuarioFormActivity.EXTRA_USUARIO_JSON, gson.toJson(usuario));
                        startActivity(intent);
                    } else if (usuario.getId().equals(miId)) {
                        Toast.makeText(this, R.string.usuarios_no_eliminarse, Toast.LENGTH_LONG).show();
                    } else {
                        confirmarEliminar(usuario);
                    }
                })
                .show();
    }

    private void confirmarEliminar(Usuario usuario) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.gastos_eliminar)
                .setMessage(getString(R.string.usuarios_eliminar_pregunta, usuario.getName()))
                .setPositiveButton(R.string.gastos_eliminar, (d, w) -> eliminar(usuario))
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void eliminar(Usuario usuario) {
        progreso.setVisibility(View.VISIBLE);
        userRepository.eliminar(usuario.getId(), new Resultado<Void>() {
            @Override
            public void onExito(Void datos) {
                Toast.makeText(UsuariosActivity.this, R.string.usuarios_eliminado, Toast.LENGTH_SHORT).show();
                cargarUsuarios();
            }

            @Override
            public void onError(String mensaje) {
                progreso.setVisibility(View.GONE);
                Toast.makeText(UsuariosActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }
}
