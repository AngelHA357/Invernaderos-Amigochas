package org.itson.ExposicionDatos.exceptions;

public class ExposicionDatosException extends Exception {
    /**
     * Constructor.
     */
    public ExposicionDatosException() {
    }

    /**
     * Constructor que recibe el mensaje de la excepción.
     *
     * @param message El mensaje de la excepción
     */
    public ExposicionDatosException(String message) {
        super(message);
    }

}