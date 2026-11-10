package com.gcastanom.oye.ui.gastos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.Gasto;
import com.gcastanom.oye.data.model.GastoRequest;
import com.gcastanom.oye.data.repository.GastoRepository;
import com.gcastanom.oye.data.repository.Resultado;
import com.gcastanom.oye.ui.Formato;
import com.gcastanom.oye.ui.Pantalla;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Locale;

/**
 * Formulario para crear o editar un gasto (equivale a Screen6 de Kodular).
 * - Sin extra: crea con POST /api/gastos.
 * - Con EXTRA_GASTO_ID: carga el gasto con GET /api/gastos/{id} y guarda con PUT.
 * El IVA que se ve aquí es solo una vista previa: el valor oficial lo calcula la API.
 */
public class GastoFormActivity extends AppCompatActivity {

    public static final String EXTRA_GASTO_ID = "gasto_id";

    private static final BigDecimal TASA_IVA_VISTA_PREVIA = new BigDecimal("0.19");

    private TextInputLayout tilFecha, tilValor, tilLugar, tilDescripcion;
    private TextInputEditText etFecha, etValor, etLugar, etDescripcion;
    private TextView tvIva, tvTotal, tvEstado;
    private Button btnGuardar;
    private ProgressBar progreso;

    private GastoRepository gastoRepository;
    private String gastoId;   // null = gasto nuevo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasto_form);
        Pantalla.aplicarMargenes(findViewById(R.id.main));

        gastoRepository = new GastoRepository(this);
        gastoId = getIntent().getStringExtra(EXTRA_GASTO_ID);

        tilFecha = findViewById(R.id.tilFecha);
        tilValor = findViewById(R.id.tilValor);
        tilLugar = findViewById(R.id.tilLugar);
        tilDescripcion = findViewById(R.id.tilDescripcion);
        etFecha = findViewById(R.id.etFecha);
        etValor = findViewById(R.id.etValor);
        etLugar = findViewById(R.id.etLugar);
        etDescripcion = findViewById(R.id.etDescripcion);
        tvIva = findViewById(R.id.tvIva);
        tvTotal = findViewById(R.id.tvTotal);
        tvEstado = findViewById(R.id.tvEstado);
        btnGuardar = findViewById(R.id.btnGuardar);
        progreso = findViewById(R.id.progreso);

        TextView tvTitulo = findViewById(R.id.tvTitulo);
        tvTitulo.setText(gastoId == null ? R.string.gasto_titulo_nuevo : R.string.gasto_titulo_editar);

        etFecha.setOnClickListener(v -> elegirFecha());
        etValor.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void afterTextChanged(Editable s) { actualizarVistaPrevia(); }
        });
        btnGuardar.setOnClickListener(v -> guardar());

        if (gastoId == null) {
            etFecha.setText(hoy());
            actualizarVistaPrevia();
        } else {
            cargarGasto();
        }
    }

    // ---------- Cargar (solo al editar) ----------

    private void cargarGasto() {
        mostrarCargando(true);
        gastoRepository.consultar(gastoId, new Resultado<Gasto>() {
            @Override
            public void onExito(Gasto gasto) {
                mostrarCargando(false);
                tvEstado.setText("");
                etFecha.setText(gasto.getFecha());
                etValor.setText(gasto.getValorTotalSinIva().toPlainString());
                etLugar.setText(gasto.getLugar());
                etDescripcion.setText(gasto.getDescripcion());
            }

            @Override
            public void onError(String mensaje) {
                mostrarCargando(false);
                tvEstado.setText(mensaje);
                btnGuardar.setEnabled(false);
            }
        });
    }

    // ---------- Guardar ----------

    private void guardar() {
        String fecha = texto(etFecha).trim();
        String lugar = texto(etLugar).trim();
        String descripcion = texto(etDescripcion).trim();
        BigDecimal valor = leerValor();

        tilFecha.setError(null);
        tilValor.setError(null);
        tilLugar.setError(null);
        tilDescripcion.setError(null);

        boolean ok = true;
        if (fecha.isEmpty()) {
            tilFecha.setError(getString(R.string.error_campo_obligatorio));
            ok = false;
        }
        if (valor == null || valor.signum() < 0) {
            tilValor.setError(getString(R.string.error_valor));
            ok = false;
        }
        if (lugar.isEmpty()) {
            tilLugar.setError(getString(R.string.error_campo_obligatorio));
            ok = false;
        }
        if (descripcion.isEmpty()) {
            tilDescripcion.setError(getString(R.string.error_campo_obligatorio));
            ok = false;
        }
        if (!ok) {
            return;
        }

        GastoRequest datos = new GastoRequest(fecha, valor, lugar, descripcion);
        Resultado<Gasto> alTerminar = new Resultado<Gasto>() {
            @Override
            public void onExito(Gasto gasto) {
                mostrarCargando(false);
                Toast.makeText(GastoFormActivity.this,
                        getString(R.string.gasto_guardado, Formato.dinero(gasto.getValorTotalConIva())),
                        Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String mensaje) {
                mostrarCargando(false);
                tvEstado.setText(mensaje);
            }
        };

        mostrarCargando(true);
        if (gastoId == null) {
            gastoRepository.registrar(datos, alTerminar);
        } else {
            gastoRepository.actualizar(gastoId, datos, alTerminar);
        }
    }

    // ---------- Vista previa del IVA ----------

    private void actualizarVistaPrevia() {
        BigDecimal valor = leerValor();
        if (valor == null) {
            valor = BigDecimal.ZERO;
        }
        BigDecimal iva = valor.multiply(TASA_IVA_VISTA_PREVIA).setScale(2, RoundingMode.HALF_UP);
        tvIva.setText(getString(R.string.gasto_iva, Formato.dinero(iva)));
        tvTotal.setText(getString(R.string.gasto_total, Formato.dinero(valor.add(iva))));
    }

    /** Acepta "10000", "10000.50" o "10000,50". Devuelve null si no es un número. */
    private BigDecimal leerValor() {
        String crudo = texto(etValor).trim().replace(',', '.');
        if (crudo.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(crudo);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ---------- Fecha ----------

    private void elegirFecha() {
        Calendar c = Calendar.getInstance();
        String actual = texto(etFecha);
        if (actual.length() == 10) {
            c.set(Integer.parseInt(actual.substring(0, 4)),
                    Integer.parseInt(actual.substring(5, 7)) - 1,
                    Integer.parseInt(actual.substring(8, 10)));
        }
        new DatePickerDialog(this,
                (vista, anio, mes, dia) -> etFecha.setText(formatoIso(anio, mes + 1, dia)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private static String hoy() {
        Calendar c = Calendar.getInstance();
        return formatoIso(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    /** La API recibe las fechas como "yyyy-MM-dd". */
    private static String formatoIso(int anio, int mes, int dia) {
        return String.format(Locale.US, "%04d-%02d-%02d", anio, mes, dia);
    }

    // ---------- Utilidades ----------

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
