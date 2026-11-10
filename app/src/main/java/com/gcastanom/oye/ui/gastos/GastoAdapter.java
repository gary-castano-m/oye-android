package com.gcastanom.oye.ui.gastos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.Gasto;
import com.gcastanom.oye.ui.Formato;

import java.util.ArrayList;
import java.util.List;

/**
 * Llena cada fila de la lista de gastos (layout item_gasto.xml).
 * RecyclerView reutiliza las filas que salen de la pantalla, por eso cada fila
 * se "vuelve a pintar" en onBindViewHolder con el gasto que le toca.
 */
public class GastoAdapter extends RecyclerView.Adapter<GastoAdapter.FilaGasto> {

    /** Qué hacer cuando tocan una fila. */
    public interface AlTocarGasto {
        void alTocar(Gasto gasto);
    }

    private final List<Gasto> gastos = new ArrayList<>();
    private final AlTocarGasto alTocar;
    private final boolean mostrarUsuario;

    public GastoAdapter(boolean mostrarUsuario, AlTocarGasto alTocar) {
        this.mostrarUsuario = mostrarUsuario;
        this.alTocar = alTocar;
    }

    public void setGastos(List<Gasto> nuevos) {
        gastos.clear();
        if (nuevos != null) {
            gastos.addAll(nuevos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilaGasto onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        View vista = LayoutInflater.from(padre.getContext()).inflate(R.layout.item_gasto, padre, false);
        return new FilaGasto(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull FilaGasto fila, int posicion) {
        Gasto gasto = gastos.get(posicion);
        fila.tvLugar.setText(gasto.getLugar());
        fila.tvTotal.setText(Formato.dinero(gasto.getValorTotalConIva()));
        fila.tvFecha.setText(Formato.fecha(gasto.getFecha()));
        fila.tvDescripcion.setText(gasto.getDescripcion());
        fila.tvUsuario.setVisibility(mostrarUsuario ? View.VISIBLE : View.GONE);
        fila.tvUsuario.setText(gasto.getNombreUsuario());
        fila.itemView.setOnClickListener(v -> alTocar.alTocar(gasto));
    }

    @Override
    public int getItemCount() {
        return gastos.size();
    }

    /** Guarda las referencias a los textos de una fila para no buscarlas cada vez. */
    static class FilaGasto extends RecyclerView.ViewHolder {
        final TextView tvLugar, tvTotal, tvFecha, tvDescripcion, tvUsuario;

        FilaGasto(View vista) {
            super(vista);
            tvLugar = vista.findViewById(R.id.tvLugar);
            tvTotal = vista.findViewById(R.id.tvTotal);
            tvFecha = vista.findViewById(R.id.tvFecha);
            tvDescripcion = vista.findViewById(R.id.tvDescripcion);
            tvUsuario = vista.findViewById(R.id.tvUsuario);
        }
    }
}
