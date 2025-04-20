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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RabbitMQReceptor {

    @Autowired
    private ProcesadorLecturas procesadorLecturas;
    private static final String QUEUE_NAME = "lecturas_crudas";
    private final ConcurrentHashMap<String, LecturaDTO> lecturasPorSensor = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Boolean> estadoSensores = new ConcurrentHashMap<>();

    @Autowired
    private ClienteGestionSensoresGrpc clienteGestionSensoresGrpc;

    @PostConstruct
    public void init() {
        actualizarEstados();

        // Iniciar el guardador de lecturas con nuestra referencia atómica
        procesadorLecturas.iniciar(lecturasPorSensor);

        // Thread para recibir mensajes de RabbitMQ
        new Thread(() -> {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                System.out.println("Esperando lecturas...");

                // Consumidor de RabbitMQ - recibe lecturas continuamente
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String mensaje = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    Gson gson = new Gson();
                    LecturaDTO lectura = gson.fromJson(mensaje, LecturaDTO.class);
                    // Si el valor asociado al ID sensor es positivo, está activo, si no, está desactivado.
                    if (estadoSensores.containsKey(lectura.getIdSensor()) && estadoSensores.get(lectura.getIdSensor())) {
                        lecturasPorSensor.put(lectura.getIdSensor(), lectura);
                        System.out.println("Lectura recibida");
                    } else if (!estadoSensores.containsKey(lectura.getIdSensor())) {
                        lecturasPorSensor.put(lectura.getIdSensor(), lectura);
                        System.out.println("Lectura anónima recibida");
                    }
                };

                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
                });

                // Esperar indefinidamente para mantener el hilo principal vivo
                Thread.currentThread().join();
            } catch (Exception e) {
                System.err.println("Error en el receptor: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Método para actualizar los estados de los sensores al iniciar el receptor.
     * Se obtienen todos los sensores y se almacenan en un mapa concurrente.
     */
    public void actualizarEstados() {
        // Obtener todos los sensores al iniciar el receptor
        SensoresRespuesta sensores = clienteGestionSensoresGrpc.obtenerTodosSensores();
        for (SensorRespuesta sensor : sensores.getSensoresList()) {
            estadoSensores.put(sensor.getIdSensor(), sensor.getEstado());
        }
    }
}