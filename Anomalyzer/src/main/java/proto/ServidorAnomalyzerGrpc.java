package proto;

import dtos.AlarmaDTO;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.bson.types.ObjectId;
import org.itson.Anomalyzer.Analizador;
import org.itson.Anomalyzer.Anomalyzer;
import org.itson.Anomalyzer.AnomalyzerServidorGrpc;
import org.itson.grpc.SensorLectura;
import org.itson.grpc.SensorRespuesta;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class ServidorAnomalyzerGrpc extends AnomalyzerServidorGrpc.AnomalyzerServidorImplBase {
    @Autowired
    private Analizador analizador;

    @Autowired
    ClienteGestionSensoresGrpc clienteGestionSensoresGrpc;

    @Autowired
    ClienteAlarmasGrpc clienteAlarmasGrpc;

    @Override
    public void registrarAlarma(Anomalyzer.AlarmaDTO request, StreamObserver<Anomalyzer.Empty> responseObserver) {
        System.out.println("Registrando alarma: " + request.getIdAlarma());

        List<String> sensoresId = request.getIdSensoresList();
        List<ObjectId> sensoresObjectId = new ArrayList<>();
        for (String id : sensoresId) {
            SensorRespuesta sensorRespuesta = clienteGestionSensoresGrpc.obtenerSensor(SensorLectura
                    .newBuilder()
                    .setIdSensor(id)
                    .build());
            sensoresObjectId.add(new ObjectId(sensorRespuesta.getId()));
        }

        AlarmaDTO alarmaDTO = new AlarmaDTO(
                request.getIdAlarma(),
                sensoresObjectId,
                request.getInvernadero(),
                request.getValorMinimo(),
                request.getValorMaximo(),
                request.getMagnitud(),
                request.getUnidad(),
                request.getMedioNotificacion(),
                request.getActivo()
        );

        analizador.agregarAlarma(alarmaDTO);

        responseObserver.onNext(Anomalyzer.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarAlarma(Anomalyzer.AlarmaDTO request, StreamObserver<Anomalyzer.Empty> responseObserver) {
        System.out.println("Actualizando alarma: " + request.getIdAlarma());

        List<String> sensoresId = request.getIdSensoresList();
        List<ObjectId> sensoresObjectId = new ArrayList<>();
        for (String id : sensoresId) {
            SensorRespuesta sensorRespuesta = clienteGestionSensoresGrpc.obtenerSensor(SensorLectura
                    .newBuilder()
                    .setIdSensor(id)
                    .build());
            sensoresObjectId.add(new ObjectId(sensorRespuesta.getId()));
        }

        AlarmaDTO alarmaDTO = new AlarmaDTO(
                request.getIdAlarma(),
                sensoresObjectId,
                request.getInvernadero(),
                request.getValorMinimo(),
                request.getValorMaximo(),
                request.getMagnitud(),
                request.getUnidad(),
                request.getMedioNotificacion(),
                request.getActivo()
        );

        analizador.actualizarAlarma(alarmaDTO);

        responseObserver.onNext(Anomalyzer.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void eliminarAlarma(Anomalyzer.AlarmaDTO request, StreamObserver<Anomalyzer.Empty> responseObserver) {
        System.out.println("Eliminando alarma: " + request.getIdAlarma());

        AlarmaDTO alarmaDTO = new AlarmaDTO();
        alarmaDTO.setIdAlarma(request.getIdAlarma());

        analizador.eliminarAlarma(alarmaDTO);

        responseObserver.onNext(Anomalyzer.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarAlarmas(Anomalyzer.AlarmasList request, StreamObserver<Anomalyzer.Empty> responseObserver) {
        System.out.println("Actualizando TODAS las alarmas");

        List<Anomalyzer.AlarmaDTO> alarmas = request.getAlarmasList();
        List<AlarmaDTO> alarmasDTO = new ArrayList<>();
        for (Anomalyzer.AlarmaDTO alarma : alarmas) {
            List<String> sensoresId = alarma.getIdSensoresList();
            List<ObjectId> sensoresObjectId = new ArrayList<>();
            for (String id : sensoresId) {
                SensorRespuesta sensorRespuesta = clienteGestionSensoresGrpc.obtenerSensor(SensorLectura
                        .newBuilder()
                        .setIdSensor(id)
                        .build());
                sensoresObjectId.add(new ObjectId(sensorRespuesta.getId()));
            }

            AlarmaDTO alarmaDTO = new AlarmaDTO(
                    alarma.getIdAlarma(),
                    sensoresObjectId,
                    alarma.getInvernadero(),
                    alarma.getValorMinimo(),
                    alarma.getValorMaximo(),
                    alarma.getMagnitud(),
                    alarma.getUnidad(),
                    alarma.getMedioNotificacion(),
                    alarma.getActivo()
            );
            alarmasDTO.add(alarmaDTO);
        }

        analizador.actualizarAlarmas(alarmasDTO);

        responseObserver.onNext(Anomalyzer.Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
