package com.gcastanom.oye.ui.splash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gcastanom.oye.R;
import com.gcastanom.oye.data.model.Usuario;
import com.gcastanom.oye.data.repository.AuthRepository;
import com.gcastanom.oye.data.repository.Resultado;
import com.gcastanom.oye.session.SessionManager;
import com.gcastanom.oye.ui.Navegacion;
import com.gcastanom.oye.ui.Pantalla;

import okhttp3.ResponseBody;

/**
 * Primera pantalla (equivale a Screen1 de Kodular).
 * Mientras se ve el logo, hace una petición para despertar Render.
 * Si hay una sesión guardada, la verifica con /api/users/me y salta directo al menú.
 */
public class SplashActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private SessionManager sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Pantalla.aplicarMargenes(findViewById(R.id.main));

        authRepository = new AuthRepository(this);
        sesion = new SessionManager(this);

        if (sesion.haySesion()) {
            verificarSesionGuardada();
        } else {
            despertarServidor();
        }
    }

    private void verificarSesionGuardada() {
        authRepository.verificarSesion(new Resultado<Usuario>() {
            @Override
            public void onExito(Usuario usuario) {
                sesion.actualizarUsuario(usuario);
                Navegacion.irAlMenu(SplashActivity.this);
            }

            @Override
            public void onError(String mensaje) {
                // Token vencido o sin conexión: se vuelve a iniciar sesión.
                sesion.cerrarSesion();
                Navegacion.irAlLogin(SplashActivity.this);
            }
        });
    }

    private void despertarServidor() {
        authRepository.despertar(new Resultado<ResponseBody>() {
            @Override
            public void onExito(ResponseBody datos) {
                Navegacion.irAlLogin(SplashActivity.this);
            }

            @Override
            public void onError(String mensaje) {
                // Aunque falle, se sigue al login; allí se verá el error si no hay internet.
                Navegacion.irAlLogin(SplashActivity.this);
            }
        });
    }
}
