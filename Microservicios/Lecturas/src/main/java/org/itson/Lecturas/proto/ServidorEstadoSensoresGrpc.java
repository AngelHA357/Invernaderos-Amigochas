package org.itson.Lecturas.proto;


import io.grpc.stub.StreamObserver;
import org.itson.Lecturas.RabbitMQReceptor;
import org.itson.grpc.EstadoPeticion;
import org.itson.grpc.EstadoRespuesta;
import org.itson.grpc.EstadoSensoresServidorGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServidorEstadoSensoresGrpc extends EstadoSensoresServidorGrpc.EstadoSensoresServidorImplBase {
    @Autowired
    private RabbitMQReceptor receptor;

    @Override
    public void actualizarEstado(EstadoPeticion request, StreamObserver<EstadoRespuesta> responseObserver) {
        receptor.actualizarEstado(request.getIdSensor(), request.getEstado());

        String resultado = request.getEstado() ? "Activo" : "Apagado";
        String idSensor = request.getIdSensor();

        EstadoRespuesta respuesta =
                EstadoRespuesta
                        .newBuilder()
                        .setRespuesta("Estado de " +idSensor + " ha sido actualizado a: " + resultado)
                        .build();

        responseObserver.onNext(respuesta);
        responseObserver.onCompleted();
    }
}