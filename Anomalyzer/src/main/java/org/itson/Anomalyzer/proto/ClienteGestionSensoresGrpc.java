package org.itson.Anomalyzer.proto;

import com.google.protobuf.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.itson.grpc.*;
import org.springframework.stereotype.Component;

/**
 * Clase cliente para gestionar la comunicación con el servidor gRPC de gestión de sensores.
 * Esta clase utiliza el cliente gRPC inyectado para realizar peticiones al servidor.
 *
 * @author Equipo1
 */
@Component
public class ClienteGestionSensoresGrpc {
    private GestionSensoresServidorGrpc.GestionSensoresServidorBlockingStub stub;

    /**
     * Constructor que inyecta el cliente gRPC.
     *
     * @param stub Cliente gRPC para comunicarse con el servidor de gestión de sensores.
     */
    public ClienteGestionSensoresGrpc(@GrpcClient("gestion-sensores") GestionSensoresServidorGrpc.GestionSensoresServidorBlockingStub stub) {
        this.stub = stub;
    }

    /**
     * Método para registrar un nuevo sensor.
     *
     * @param sensor Petición que contiene la información del sensor a registrar.
     * @return Respuesta con la información del sensor registrado.
     */
    public SensorRespuesta obtenerSensor(SensorLectura sensor) {
        SensorPeticion request = SensorPeticion.newBuilder().setSensorLectura(sensor).build();
        return stub.getSensor(request);
    }

    /**
     * Método para obtener todos los sensores registrados.
     *
     * @return Respuesta con la lista de todos los sensores.
     */
    public SensoresRespuesta obtenerTodosSensores() {
        return stub.getTodosSensores(Empty.newBuilder().build());
    }
}