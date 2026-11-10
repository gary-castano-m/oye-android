package com.gcastanom.oye.data.remote;

import com.gcastanom.oye.data.model.ForgotPasswordRequest;
import com.gcastanom.oye.data.model.Gasto;
import com.gcastanom.oye.data.model.GastoRequest;
import com.gcastanom.oye.data.model.LoginRequest;
import com.gcastanom.oye.data.model.LoginResponse;
import com.gcastanom.oye.data.model.PerfilRequest;
import com.gcastanom.oye.data.model.RegisterRequest;
import com.gcastanom.oye.data.model.ReporteGastos;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.data.model.UsuarioRequest;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Todos los endpoints del backend. Cada método es una petición:
 * la anotación dice el tipo (GET, POST, PUT, DELETE) y la ruta.
 * Retrofit escribe el código que la envía.
 */
public interface ApiService {

    // ---------- Público ----------

    /** Petición liviana para despertar Render mientras se ve el splash. */
    @GET("v3/api-docs")
    Call<ResponseBody> despertar();

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest body);

    @POST("api/auth/register")
    Call<Usuario> registrar(@Body RegisterRequest body);

    @POST("api/auth/forgot-password")
    Call<Void> recuperarClave(@Body ForgotPasswordRequest body);

    // ---------- Mi perfil ----------

    @GET("api/users/me")
    Call<Usuario> miPerfil();

    @PUT("api/users/me")
    Call<Usuario> actualizarPerfil(@Body PerfilRequest body);

    // ---------- Gastos ----------

    @GET("api/gastos")
    Call<List<Gasto>> listarGastos();

    @GET("api/gastos/{id}")
    Call<Gasto> consultarGasto(@Path("id") String id);

    @POST("api/gastos")
    Call<Gasto> registrarGasto(@Body GastoRequest body);

    @PUT("api/gastos/{id}")
    Call<Gasto> actualizarGasto(@Path("id") String id, @Body GastoRequest body);

    @DELETE("api/gastos/{id}")
    Call<Void> eliminarGasto(@Path("id") String id);

    /** Reporte de gastos. Los filtros en null no se envían. Fechas en formato yyyy-MM-dd. */
    @GET("api/gastos/reports")
    Call<ReporteGastos> reporteGastos(@Query("desde") String desde,
                                      @Query("hasta") String hasta,
                                      @Query("lugar") String lugar);

    // ---------- Usuarios (ADMIN) ----------

    @GET("api/users")
    Call<List<Usuario>> listarUsuarios();

    @POST("api/users")
    Call<Usuario> crearUsuario(@Body UsuarioRequest body);

    @PUT("api/users/{id}")
    Call<Usuario> actualizarUsuario(@Path("id") String id, @Body UsuarioRequest body);

    @DELETE("api/users/{id}")
    Call<Void> eliminarUsuario(@Path("id") String id);

    /** Reporte de usuarios por rol o por nombre. Los filtros en null no se envían. */
    @GET("api/users/reports")
    Call<List<Usuario>> reporteUsuarios(@Query("role") String role, @Query("name") String name);
}
