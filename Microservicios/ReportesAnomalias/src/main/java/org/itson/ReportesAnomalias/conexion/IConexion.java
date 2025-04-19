package org.itson.ReportesAnomalias.conexion;

import com.mongodb.client.MongoDatabase;

public interface IConexion {

    /**
     * Permite crear una conexión al mecanismo de persistencia.
     *
     * @return Una conexión al mecanismo de persistencia
     */
    public MongoDatabase crearConexion();

}
