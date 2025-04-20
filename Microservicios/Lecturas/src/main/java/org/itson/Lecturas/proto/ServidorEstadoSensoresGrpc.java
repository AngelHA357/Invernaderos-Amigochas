package org.itson.Lecturas.proto;


import com.google.protobuf.Empty;
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
    public void actualizarEstados(Empty request, StreamObserver<Empty> responseObserver) {
        receptor.actualizarEstados();

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}