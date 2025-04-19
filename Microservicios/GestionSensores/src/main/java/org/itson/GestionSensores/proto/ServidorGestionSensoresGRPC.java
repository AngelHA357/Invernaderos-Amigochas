package org.itson.GestionSensores.proto;


import io.grpc.stub.StreamObserver;
import org.itson.GestionSensores.collections.Invernadero;
import org.itson.GestionSensores.dtos.SensorDTO;
import org.itson.GestionSensores.excepciones.GestionSensoresException;
import org.itson.GestionSensores.service.GestionSensoresService;
import org.itson.grpc.GestionSensoresServidorGrpc;
import org.itson.grpc.SensorLectura;
import org.itson.grpc.SensorPeticion;
import org.itson.grpc.SensorRespuesta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServidorGestionSensoresGRPC extends GestionSensoresServidorGrpc.GestionSensoresServidorImplBase {
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
}