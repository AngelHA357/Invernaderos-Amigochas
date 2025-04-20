package org.itson.Lecturas;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.itson.Lecturas.dtos.LecturaDTO;
import org.itson.Lecturas.proto.ClienteGestionSensoresGrpc;
import org.itson.grpc.SensorRespuesta;
import org.itson.grpc.SensoresRespuesta;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RabbitMQReceptor {

    @Autowired
    private ProcesadorLecturas procesadorLecturas;

    private static final String QUEUE_RECEIVE = "lecturas_crudas";
    private static final String QUEUE_EMIT = "lecturas_enriquecidas";

    private final ConcurrentHashMap<String, LecturaDTO> lecturasPorSensor = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Boolean> estadoSensores = new ConcurrentHashMap<>();

    @Autowired
    private ClienteGestionSensoresGrpc clienteGestionSensoresGrpc;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final Gson gson = new Gson();

    @PostConstruct
    public void init() {
        actualizarEstados();
        procesadorLecturas.iniciar(lecturasPorSensor);

        new Thread(() -> {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(QUEUE_RECEIVE, false, false, false, null);
                System.out.println("Esperando lecturas...");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String mensaje = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    LecturaDTO lectura = gson.fromJson(mensaje, LecturaDTO.class);

                    // Si el sensor está activo o es nuevo (anónimo), se procesa
                    if (!estadoSensores.containsKey(lectura.getIdSensor()) || estadoSensores.get(lectura.getIdSensor())) {
                        lecturasPorSensor.put(lectura.getIdSensor(), lectura);
                        System.out.println("Lectura recibida");

                        try {
                            // Enriquecer la lectura usando gRPC
                            SensorRespuesta sensorInfo = clienteGestionSensoresGrpc.obtenerSensor(lectura.toSensorLectura());

                            // Actualizar lectura con datos del sensor
                            lectura.setMacAddress(sensorInfo.getMacAddress());
                            lectura.setMarca(sensorInfo.getMarca());
                            lectura.setModelo(sensorInfo.getModelo());
                            lectura.setMagnitud(sensorInfo.getMagnitud());
                            lectura.setUnidad(sensorInfo.getUnidad());
                            lectura.setInvernadero(sensorInfo.getNombreInvernadero());
                            lectura.setSector(sensorInfo.getSector());
                            lectura.setFila(sensorInfo.getFila());

                            // Serializar y enviar a cola enriquecida
                            String lecturaEnriquecidaJson = gson.toJson(lectura);
                            rabbitTemplate.convertAndSend(QUEUE_EMIT, lecturaEnriquecidaJson);
                            System.out.println("Lectura enriquecida enviada");

                        } catch (Exception e) {
                            System.err.println("Error al enriquecer la lectura: " + e.getMessage());
                        }

                    } else {
                        System.out.println("Lectura descartada por sensor inactivo: " + lectura.getIdSensor());
                    }
                };

                channel.basicConsume(QUEUE_RECEIVE, true, deliverCallback, consumerTag -> {});
                Thread.currentThread().join();

            } catch (Exception e) {
                System.err.println("Error en el receptor: " + e.getMessage());
            }
        }).start();
    }

    public void actualizarEstados() {
        SensoresRespuesta sensores = clienteGestionSensoresGrpc.obtenerTodosSensores();
        for (SensorRespuesta sensor : sensores.getSensoresList()) {
            estadoSensores.put(sensor.getIdSensor(), sensor.getEstado());
        }
    }
}