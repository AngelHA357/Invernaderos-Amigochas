package org.itson.GestionSensores.proto;


import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.itson.GestionSensores.collections.Invernadero;
import org.itson.GestionSensores.dtos.SensorDTO;
import org.itson.GestionSensores.excepciones.GestionSensoresException;
import org.itson.GestionSensores.service.GestionSensoresService;
import org.itson.grpc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServidorGestionSensoresGrpc extends GestionSensoresServidorGrpc.GestionSensoresServidorImplBase {
    @Autowired
    private GestionSensoresService gestionSensoresService;

    @Override
    public void getSensor(SensorPeticion request, StreamObserver<SensorRespuesta> responseObserver) {
        SensorDTO sensor = null;
        SensorLectura sensorLectura = request.getSensorLectura();
        try {
            sensor = gestionSensoresService.obtenerSensorPorId(sensorLectura.getIdSensor());
        } catch (GestionSensoresException e) {
            try {
                // Si llega aquí es porque el sensor no está registrado
                Invernadero invernadero = gestionSensoresService.obtenerInvernaderoPorNombre("N/A");
                sensor = new SensorDTO(
                        sensorLectura.getIdSensor(),
                        sensorLectura.getMacAddress(),
                        sensorLectura.getMarca(),
                        sensorLectura.getModelo(),
                        sensorLectura.getTipoSensor(),
                        sensorLectura.getMagnitud(),
                        invernadero.get_id(),
                        invernadero.getNombre(),
                        invernadero.getSectores().getFirst(),
                        invernadero.getFilas().getFirst()
                );
                sensor = gestionSensoresService.registrarSensor(sensor);
            } catch (GestionSensoresException ex) {
                throw new RuntimeException(ex);
            }
        }

        // Si llegamos aquí, todo está bien
        SensorRespuesta response = SensorRespuesta.newBuilder()
                .setIdSensor(sensor.getIdSensor())
                .setMacAddress(sensor.getMacAddress())
                .setMarca(sensor.getMarca())
                .setModelo(sensor.getModelo())
                .setTipoSensor(sensor.getTipoSensor())
                .setMagnitud(sensor.getMagnitud())
                .setIdInvernadero(sensor.getIdInvernadero())
                .setNombreInvernadero(sensor.getNombreInvernadero())
                .setSector(sensor.getSector())
                .setFila(sensor.getFila())
                .setEstado(sensor.isEstado())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getTodosSensores(Empty request, StreamObserver<SensoresRespuesta> responseObserver) {
        try {
            // Obtiene la lista de sensores desde el servicio
            List<SensorDTO> sensores = gestionSensoresService.obtenerTodosSensores();

            // Construye la respuesta con la lista de sensores
            SensoresRespuesta.Builder respuestaBuilder = SensoresRespuesta.newBuilder();
            for (SensorDTO sensor : sensores) {
                SensorRespuesta sensorRespuesta = SensorRespuesta.newBuilder()
                        .setIdSensor(sensor.getIdSensor())
                        .setMacAddress(sensor.getMacAddress())
                        .setMarca(sensor.getMarca())
                        .setModelo(sensor.getModelo())
                        .setTipoSensor(sensor.getTipoSensor())
                        .setMagnitud(sensor.getMagnitud())
                        .setIdInvernadero(sensor.getIdInvernadero())
                        .setNombreInvernadero(sensor.getNombreInvernadero())
                        .setSector(sensor.getSector())
                        .setFila(sensor.getFila())
                        .setEstado(sensor.isEstado())
                        .build();

                // Agrega cada sensor a la lista de la respuesta
                respuestaBuilder.addSensores(sensorRespuesta);
            }

            // Envía la respuesta completa y cierra el stream
            responseObserver.onNext(respuestaBuilder.build());
            responseObserver.onCompleted();
        } catch (GestionSensoresException e) {
            // Manejo de excepciones
            responseObserver.onError(e);
        }
    }
}