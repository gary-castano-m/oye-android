package com.gcastanom.oye.data.repository;

/**
 * Lo que una pantalla recibe cuando termina una petición:
 * los datos si salió bien, o un mensaje listo para mostrar si salió mal.
 */
public interface Resultado<T> {

    void onExito(T datos);

    void onError(String mensaje);
}
