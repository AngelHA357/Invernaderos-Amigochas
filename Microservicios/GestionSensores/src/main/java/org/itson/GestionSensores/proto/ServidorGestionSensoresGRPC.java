package org.itson.GestionSensores.proto;

import com.itson.grpc.GestionSensoresServicioGrpc;
import com.itson.grpc.SensorPeticion;
import com.itson.grpc.SensorRespuesta;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.itson.GestionSensores.collections.Invernadero;
import org.itson.GestionSensores.collections.Sensor;
import org.itson.GestionSensores.persistence.IGestionSensoresRepository;
import org.itson.GestionSensores.persistence.IInvernaderosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServidorGestionSensoresGRPC extends GestionSensoresServicioGrpc.GestionSensoresServicioImplBase {
    @Autowired
    private IGestionSensoresRepository gestionSensoresRepository;

    @Autowired
    private IInvernaderosRepository invernaderosRepository;

    @Override
    public void getSensorByMac(SensorPeticion request, StreamObserver<SensorRespuesta> responseObserver) {
        Optional<Sensor> sensorOpt = gestionSensoresRepository.findByMacAddress(request.getMacAddress());

        if (sensorOpt.isEmpty()) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Sensor no encontrado")
                    .asRuntimeException());
            return;
        }

        Sensor sensor = sensorOpt.get();
        Invernadero inv = invernaderosRepository.findById(sensor.getIdInvernadero()).orElse(null);

        assert inv != null;
        SensorRespuesta response = SensorRespuesta.newBuilder()
                .setMacAddress(sensor.getMacAddress())
                .setMarca(sensor.getMarca())
                .setModelo(sensor.getModelo())
                .setIdInvernadero(inv.get_id())
                .setNombreInvernadero(inv.getNombre())
                .setSector(sensor.getSector())
                .setFila(sensor.getFila())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}