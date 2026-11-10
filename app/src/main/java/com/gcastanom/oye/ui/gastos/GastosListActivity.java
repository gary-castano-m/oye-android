package com.gcastanom.oye.ui.gastos;

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
import com.gcastanom.oye.data.model.Gasto;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.data.repository.GastoRepository;
import com.gcastanom.oye.data.repository.Resultado;
import com.gcastanom.oye.sensors.ShakeDetector;
import com.gcastanom.oye.session.SessionManager;
import com.gcastanom.oye.ui.Formato;
import com.gcastanom.oye.ui.Pantalla;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lista de gastos (equivale a Screen7 de Kodular).
 * GET /api/gastos al abrir y cada vez que se vuelve a esta pantalla;
 * al tocar un gasto se puede editar o eliminar (DELETE /api/gastos/{id}).
 * Sensor acelerómetro: al agitar el celular se vuelve a pedir la lista.
 */
public class GastosListActivity extends AppCompatActivity {

    private GastoRepository gastoRepository;
    private GastoAdapter adapter;
    private ProgressBar progreso;
    private TextView tvResumen;
    private TextView tvVacio;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos_list);
        Pantalla.aplicarMargenes(findViewById(R.id.main));

        gastoRepository = new GastoRepository(this);
        progreso = findViewById(R.id.progreso);
        tvResumen = findViewById(R.id.tvResumen);
        tvVacio = findViewById(R.id.tvVacio);

        // ADMIN y REVIEWER ven gastos de todos: a ellos se les muestra de quién es cada uno.
        Usuario usuario = new SessionManager(this).getUsuario();
        boolean veTodos = usuario != null && !Usuario.ROL_MEMBER.equals(usuario.getRole());

        adapter = new GastoAdapter(veTodos, this::mostrarOpciones);
        RecyclerView rvGastos = findViewById(R.id.rvGastos);
        rvGastos.setLayoutManager(new LinearLayoutManager(this));
        rvGastos.setAdapter(adapter);

        findViewById(R.id.btnNuevoGasto).setOnClickListener(
                v -> startActivity(new Intent(this, GastoFormActivity.class)));

        shakeDetector = new ShakeDetector(this, () -> {
            Toast.makeText(this, R.string.gastos_actualizando, Toast.LENGTH_SHORT).show();
            cargarGastos();
        });
        findViewById(R.id.tvAgitar).setVisibility(shakeDetector.disponible() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarGastos();            // se recarga al volver de crear o editar
        shakeDetector.iniciar();   // el acelerómetro escucha solo con la pantalla visible
    }

    @Override
    protected void onPause() {
        super.onPause();
        shakeDetector.detener();
    }

    void cargarGastos() {
        progreso.setVisibility(View.VISIBLE);
        gastoRepository.listar(new Resultado<List<Gasto>>() {
            @Override
            public void onExito(List<Gasto> gastos) {
                progreso.setVisibility(View.GONE);
                adapter.setGastos(gastos);
                mostrarResumen(gastos);
            }

            @Override
            public void onError(String mensaje) {
                progreso.setVisibility(View.GONE);
                tvResumen.setText(mensaje);
            }
        });
    }

    private void mostrarResumen(List<Gasto> gastos) {
        BigDecimal total = BigDecimal.ZERO;
        for (Gasto g : gastos) {
            if (g.getValorTotalConIva() != null) {
                total = total.add(g.getValorTotalConIva());
            }
        }
        tvResumen.setText(getString(R.string.gastos_resumen, gastos.size(), Formato.dinero(total)));
        tvVacio.setVisibility(gastos.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void mostrarOpciones(Gasto gasto) {
        String[] opciones = {getString(R.string.gastos_editar), getString(R.string.gastos_eliminar)};
        new MaterialAlertDialogBuilder(this)
                .setTitle(gasto.getLugar())
                .setItems(opciones, (d, cual) -> {
                    if (cual == 0) {
                        Intent intent = new Intent(this, GastoFormActivity.class);
                        intent.putExtra(GastoFormActivity.EXTRA_GASTO_ID, gasto.getId());
                        startActivity(intent);
                    } else {
                        confirmarEliminar(gasto);
                    }
                })
                .show();
    }

    private void confirmarEliminar(Gasto gasto) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.gastos_eliminar)
                .setMessage(getString(R.string.gastos_eliminar_pregunta,
                        gasto.getLugar(), Formato.dinero(gasto.getValorTotalConIva())))
                .setPositiveButton(R.string.gastos_eliminar, (d, w) -> eliminar(gasto))
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void eliminar(Gasto gasto) {
        progreso.setVisibility(View.VISIBLE);
        gastoRepository.eliminar(gasto.getId(), new Resultado<Void>() {
            @Override
            public void onExito(Void datos) {
                Toast.makeText(GastosListActivity.this, R.string.gastos_eliminado, Toast.LENGTH_SHORT).show();
                cargarGastos();
            }

            @Override
            public void onError(String mensaje) {
                progreso.setVisibility(View.GONE);
                Toast.makeText(GastosListActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }
}
