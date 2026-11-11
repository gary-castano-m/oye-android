package com.gcastanom.oye.ui.usuarios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.ui.Etiquetas;

import java.util.ArrayList;
import java.util.List;

/** Llena cada fila de una lista de usuarios (layout item_usuario.xml). Se usa en el CRUD y en el reporte. */
public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.FilaUsuario> {

    /** Qué hacer cuando tocan una fila. Puede ser null (por ejemplo, en el reporte). */
    public interface AlTocarUsuario {
        void alTocar(Usuario usuario);
    }

    private final List<Usuario> usuarios = new ArrayList<>();
    private final AlTocarUsuario alTocar;

    public UsuarioAdapter(AlTocarUsuario alTocar) {
        this.alTocar = alTocar;
    }

    public void setUsuarios(List<Usuario> nuevos) {
        usuarios.clear();
        if (nuevos != null) {
            usuarios.addAll(nuevos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilaUsuario onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        View vista = LayoutInflater.from(padre.getContext()).inflate(R.layout.item_usuario, padre, false);
        return new FilaUsuario(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull FilaUsuario fila, int posicion) {
        Usuario u = usuarios.get(posicion);
        fila.tvNombre.setText(u.getName());
        fila.tvCuenta.setText(fila.itemView.getContext().getString(
                R.string.usuario_cuenta, u.getUsername(), u.getEmail()));
        fila.tvRolEstado.setText(fila.itemView.getContext().getString(R.string.usuario_rol_estado,
                Etiquetas.rol(fila.itemView.getContext(), u.getRole()),
                Etiquetas.estado(fila.itemView.getContext(), u.getStatus())));
        if (alTocar != null) {
            fila.itemView.setOnClickListener(v -> alTocar.alTocar(u));
        }
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    static class FilaUsuario extends RecyclerView.ViewHolder {
        final TextView tvNombre, tvCuenta, tvRolEstado;

        FilaUsuario(View vista) {
            super(vista);
            tvNombre = vista.findViewById(R.id.tvNombre);
            tvCuenta = vista.findViewById(R.id.tvCuenta);
            tvRolEstado = vista.findViewById(R.id.tvRolEstado);
        }
    }
}
