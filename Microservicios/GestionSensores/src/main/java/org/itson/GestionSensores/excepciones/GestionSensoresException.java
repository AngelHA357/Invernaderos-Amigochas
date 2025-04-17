package org.itson.GestionSensores.excepciones;

/**
 * Clase que permite el manejo de errores provocados en el microservicio de gestión de sensores.
 *
 * @author Equipo1
 */
public class GestionSensoresException extends Exception {

    /**
     * Constructor.
     */
    public GestionSensoresException() {
    }

    /**
     * Constructor que recibe el mensaje de la excepción.
     *
     * @param message El mensaje de la excepción
     */
    public GestionSensoresException(String message) {
        super(message);
    }

}