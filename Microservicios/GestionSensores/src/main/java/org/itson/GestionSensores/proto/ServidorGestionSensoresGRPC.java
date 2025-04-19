package org.itson.GestionSensores.proto;

import com.itson.grpc.GestionSensoresServicioGrpc;
import com.itson.grpc.SensorLectura;
import com.itson.grpc.SensorPeticion;
import com.itson.grpc.SensorRespuesta;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.validation.constraints.Null;
import org.bson.types.ObjectId;
import org.itson.GestionSensores.collections.Invernadero;
import org.itson.GestionSensores.collections.Sensor;
import org.itson.GestionSensores.dtos.SensorDTO;
import org.itson.GestionSensores.excepciones.GestionSensoresException;
import org.itson.GestionSensores.persistence.IInvernaderosRepository;
import org.itson.GestionSensores.service.GestionSensoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServidorGestionSensoresGRPC extends GestionSensoresServicioGrpc.GestionSensoresServicioImplBase {
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
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}