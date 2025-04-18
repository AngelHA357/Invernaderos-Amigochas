package org.itson.Lecturas.excepciones;

public class LecturasException extends Exception {
    /**
     * Constructor.
     */
    public LecturasException() {
    }

    /**
     * Constructor que recibe el mensaje de la excepción.
     *
     * @param message El mensaje de la excepción
     */
    public LecturasException(String message) {
        super(message);
    }

}