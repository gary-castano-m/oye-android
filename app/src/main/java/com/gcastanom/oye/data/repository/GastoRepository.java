package com.gcastanom.oye.data.repository;

import android.content.Context;

import com.gcastanom.oye.data.model.Gasto;
import com.gcastanom.oye.data.model.GastoRequest;
import com.gcastanom.oye.data.model.ReporteGastos;
import com.gcastanom.oye.data.remote.ApiService;
import com.gcastanom.oye.data.remote.RetrofitClient;

import java.util.List;

/** CRUD y reportes de la tabla Gastos. El token lo agrega el AuthInterceptor. */
public class GastoRepository {

    private final ApiService api;

    public GastoRepository(Context context) {
        this.api = RetrofitClient.getApi(context);
    }

    public void listar(Resultado<List<Gasto>> resultado) {
        api.listarGastos().enqueue(new ApiCallback<>(resultado));
    }

    public void consultar(String id, Resultado<Gasto> resultado) {
        api.consultarGasto(id).enqueue(new ApiCallback<>(resultado));
    }

    public void registrar(GastoRequest datos, Resultado<Gasto> resultado) {
        api.registrarGasto(datos).enqueue(new ApiCallback<>(resultado));
    }

    public void actualizar(String id, GastoRequest datos, Resultado<Gasto> resultado) {
        api.actualizarGasto(id, datos).enqueue(new ApiCallback<>(resultado));
    }

    public void eliminar(String id, Resultado<Void> resultado) {
        api.eliminarGasto(id).enqueue(new ApiCallback<>(resultado));
    }

    /** Reporte por fechas (desde/hasta en yyyy-MM-dd) o por lugar. Los filtros en null no se envían. */
    public void reporte(String desde, String hasta, String lugar, Resultado<ReporteGastos> resultado) {
        api.reporteGastos(desde, hasta, lugar).enqueue(new ApiCallback<>(resultado));
    }
}
