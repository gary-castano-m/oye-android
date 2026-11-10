package com.gcastanom.oye.sensors;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * SENSOR 1 · Ubicación (GPS, Wi-Fi y antenas, combinados por Google Play Services).
 * Pide UNA sola lectura de la posición y la convierte en una dirección con Geocoder.
 * En Kodular era el componente LocationSensor, que había que apagar después de la primera lectura;
 * aquí getCurrentLocation ya entrega una sola lectura y se detiene solo.
 */
public class LocationHelper {

    /** Lo que recibe la pantalla al terminar. */
    public interface AlObtenerLugar {
        void onLugar(String lugar);

        void onError(String mensaje);
    }

    /** Permisos que hay que pedirle al usuario antes de usar el sensor. */
    public static final String[] PERMISOS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final int LARGO_MAXIMO_LUGAR = 100;   // el backend acepta hasta 100 caracteres

    private final Context context;
    private final FusedLocationProviderClient ubicacion;
    private final Handler hiloPrincipal = new Handler(Looper.getMainLooper());

    public LocationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.ubicacion = LocationServices.getFusedLocationProviderClient(this.context);
    }

    public boolean tienePermiso() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /** Solo se llama después de comprobar tienePermiso(). */
    @SuppressLint("MissingPermission")
    public void obtenerLugar(AlObtenerLugar resultado) {
        ubicacion.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        resultado.onError("No se pudo leer la ubicación. Revisa que el GPS esté encendido.");
                    } else {
                        convertirEnDireccion(location, resultado);
                    }
                })
                .addOnFailureListener(e ->
                        resultado.onError("No se pudo leer la ubicación. Revisa que el GPS esté encendido."));
    }

    /** Geocoder traduce latitud/longitud a una dirección escrita. Si falla, se usan las coordenadas. */
    @SuppressWarnings("deprecation")
    private void convertirEnDireccion(Location location, AlObtenerLugar resultado) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        if (!Geocoder.isPresent()) {
            resultado.onLugar(coordenadas(lat, lng));
            return;
        }
        Geocoder geocoder = new Geocoder(context, Locale.forLanguageTag("es-CO"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 o superior: el Geocoder avisa cuando termina.
            geocoder.getFromLocation(lat, lng, 1, new Geocoder.GeocodeListener() {
                @Override
                public void onGeocode(List<Address> direcciones) {
                    hiloPrincipal.post(() -> entregar(direcciones, lat, lng, resultado));
                }

                @Override
                public void onError(String mensaje) {
                    hiloPrincipal.post(() -> resultado.onLugar(coordenadas(lat, lng)));
                }
            });
        } else {
            // Android 12 o inferior: la búsqueda va en un hilo aparte para no congelar la pantalla.
            new Thread(() -> {
                try {
                    List<Address> direcciones = geocoder.getFromLocation(lat, lng, 1);
                    hiloPrincipal.post(() -> entregar(direcciones, lat, lng, resultado));
                } catch (IOException e) {
                    hiloPrincipal.post(() -> resultado.onLugar(coordenadas(lat, lng)));
                }
            }).start();
        }
    }

    private void entregar(List<Address> direcciones, double lat, double lng, AlObtenerLugar resultado) {
        if (direcciones == null || direcciones.isEmpty() || direcciones.get(0).getAddressLine(0) == null) {
            resultado.onLugar(coordenadas(lat, lng));
            return;
        }
        String direccion = direcciones.get(0).getAddressLine(0);
        if (direccion.length() > LARGO_MAXIMO_LUGAR) {
            direccion = direccion.substring(0, LARGO_MAXIMO_LUGAR);
        }
        resultado.onLugar(direccion);
    }

    private static String coordenadas(double lat, double lng) {
        return String.format(Locale.US, "Lat %.5f, Lng %.5f", lat, lng);
    }
}
