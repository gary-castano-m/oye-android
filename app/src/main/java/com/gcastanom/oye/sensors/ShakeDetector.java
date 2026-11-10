package com.gcastanom.oye.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * SENSOR 2 · Acelerómetro.
 * Mide la aceleración en los ejes x, y, z. Quieto, el celular marca 1 g (la gravedad).
 * Si la fuerza total pasa de 2,5 g, se considera que lo agitaron.
 * En Kodular era el evento AccelerometerSensor.Shaking.
 */
public class ShakeDetector implements SensorEventListener {

    /** Qué hacer cuando agitan el celular. */
    public interface AlAgitar {
        void alAgitar();
    }

    private static final float UMBRAL_G = 2.5f;
    private static final long ESPERA_ENTRE_AGITONES_MS = 1500;   // evita recargar varias veces seguidas

    private final SensorManager sensorManager;
    private final Sensor acelerometro;
    private final AlAgitar alAgitar;
    private long ultimoAgiton;

    public ShakeDetector(Context context, AlAgitar alAgitar) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.acelerometro = sensorManager == null
                ? null
                : sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.alAgitar = alAgitar;
    }

    public boolean disponible() {
        return acelerometro != null;
    }

    /** Empieza a escuchar. Se llama en onResume: solo mientras la pantalla está visible. */
    public void iniciar() {
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_UI);
        }
    }

    /** Deja de escuchar. Se llama en onPause, para no gastar batería. */
    public void detener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent evento) {
        float gx = evento.values[0] / SensorManager.GRAVITY_EARTH;
        float gy = evento.values[1] / SensorManager.GRAVITY_EARTH;
        float gz = evento.values[2] / SensorManager.GRAVITY_EARTH;
        double fuerza = Math.sqrt(gx * gx + gy * gy + gz * gz);

        if (fuerza > UMBRAL_G) {
            long ahora = System.currentTimeMillis();
            if (ahora - ultimoAgiton > ESPERA_ENTRE_AGITONES_MS) {
                ultimoAgiton = ahora;
                alAgitar.alAgitar();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int precision) {
        // No se necesita.
    }
}
