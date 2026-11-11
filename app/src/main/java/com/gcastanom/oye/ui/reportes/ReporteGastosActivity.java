package com.gcastanom.oye.ui.reportes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.ReporteGastos;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.data.repository.GastoRepository;
import com.gcastanom.oye.data.repository.Resultado;
import com.gcastanom.oye.session.SessionManager;
import com.gcastanom.oye.ui.Fechas;
import com.gcastanom.oye.ui.Formato;
import com.gcastanom.oye.ui.Pantalla;
import com.gcastanom.oye.ui.gastos.GastoAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Los dos reportes parametrizados de la tabla Gastos (Screen9 y Screen10 de Kodular, unidos):
 * - Por rango de fechas: GET /api/gastos/reports?desde=...&hasta=...
 * - Por lugar:           GET /api/gastos/reports?lugar=...
 * La app solo arma la URL; filtrar, contar y sumar lo hace PostgreSQL en el servidor.
 */
public class ReporteGastosActivity extends AppCompatActivity {

    private RadioGroup rgTipo;
    private View grupoFechas, grupoLugar, tarjetaTotales;
    private TextInputLayout tilDesde, tilHasta, tilLugar;
    private TextInputEditText etDesde, etHasta, etLugar;
    private TextView tvCantidad, tvTotalSinIva, tvTotalIva, tvTotalConIva, tvEstado;
    private Button btnGenerar;
    private ProgressBar progreso;
    private GastoAdapter adapter;

    private GastoRepository gastoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_gastos);
        Pantalla.aplicarMargenes(findViewById(R.id.main));

        gastoRepository = new GastoRepository(this);

        rgTipo = findViewById(R.id.rgTipo);
        grupoFechas = findViewById(R.id.grupoFechas);
        grupoLugar = findViewById(R.id.grupoLugar);
        tarjetaTotales = findViewById(R.id.tarjetaTotales);
        tilDesde = findViewById(R.id.tilDesde);
        tilHasta = findViewById(R.id.tilHasta);
        tilLugar = findViewById(R.id.tilLugar);
        etDesde = findViewById(R.id.etDesde);
        etHasta = findViewById(R.id.etHasta);
        etLugar = findViewById(R.id.etLugar);
        tvCantidad = findViewById(R.id.tvCantidad);
        tvTotalSinIva = findViewById(R.id.tvTotalSinIva);
        tvTotalIva = findViewById(R.id.tvTotalIva);
        tvTotalConIva = findViewById(R.id.tvTotalConIva);
        tvEstado = findViewById(R.id.tvEstado);
        btnGenerar = findViewById(R.id.btnGenerar);
        progreso = findViewById(R.id.progreso);

        Usuario usuario = new SessionManager(this).getUsuario();
        boolean veTodos = usuario != null && !Usuario.ROL_MEMBER.equals(usuario.getRole());
        adapter = new GastoAdapter(veTodos, gasto -> { });   // en el reporte las filas solo se leen
        RecyclerView rv = findViewById(R.id.rvGastos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Por defecto: el mes actual, del día 1 hasta hoy.
        etDesde.setText(Fechas.primerDiaDelMes());
        etHasta.setText(Fechas.hoy());
        etDesde.setOnClickListener(v -> Fechas.elegir(this, etDesde));
        etHasta.setOnClickListener(v -> Fechas.elegir(this, etHasta));

        rgTipo.setOnCheckedChangeListener((grupo, id) -> mostrarFiltro());
        btnGenerar.setOnClickListener(v -> generar());
        mostrarFiltro();
    }

    private boolean porFechas() {
        return rgTipo.getCheckedRadioButtonId() == R.id.rbFechas;
    }

    private void mostrarFiltro() {
        grupoFechas.setVisibility(porFechas() ? View.VISIBLE : View.GONE);
        grupoLugar.setVisibility(porFechas() ? View.GONE : View.VISIBLE);
    }

    private void generar() {
        tilDesde.setError(null);
        tilHasta.setError(null);
        tilLugar.setError(null);

        String desde = null, hasta = null, lugar = null;
        if (porFechas()) {
            desde = texto(etDesde);
            hasta = texto(etHasta);
            if (desde.compareTo(hasta) > 0) {   // las fechas yyyy-MM-dd se pueden comparar como texto
                tilHasta.setError(getString(R.string.error_rango_fechas));
                return;
            }
        } else {
            lugar = texto(etLugar).trim();
            if (lugar.isEmpty()) {
                tilLugar.setError(getString(R.string.error_campo_obligatorio));
                return;
            }
        }

        progreso.setVisibility(View.VISIBLE);
        btnGenerar.setEnabled(false);
        tvEstado.setText(R.string.conectando);
        gastoRepository.reporte(desde, hasta, lugar, new Resultado<ReporteGastos>() {
            @Override
            public void onExito(ReporteGastos reporte) {
                terminar();
                tvEstado.setText("");
                tarjetaTotales.setVisibility(View.VISIBLE);
                tvCantidad.setText(getString(R.string.reporte_cantidad, reporte.getCantidad()));
                tvTotalSinIva.setText(getString(R.string.reporte_sin_iva, Formato.dinero(reporte.getTotalSinIva())));
                tvTotalIva.setText(getString(R.string.reporte_iva, Formato.dinero(reporte.getTotalIva())));
                tvTotalConIva.setText(getString(R.string.reporte_con_iva, Formato.dinero(reporte.getTotalConIva())));
                adapter.setGastos(reporte.getGastos());
            }

            @Override
            public void onError(String mensaje) {
                terminar();
                tvEstado.setText(mensaje);
            }
        });
    }

    private void terminar() {
        progreso.setVisibility(View.GONE);
        btnGenerar.setEnabled(true);
    }

    private static String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString();
    }
}
