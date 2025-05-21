package org.itson.Anomalyzer.service;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import org.itson.Anomalyzer.collections.Alarma;
import org.itson.Anomalyzer.collections.Anomalia;
import org.itson.Anomalyzer.dtos.AlarmaAnomaliaDTO;
import org.itson.Anomalyzer.dtos.AlarmaDTO;
import org.itson.Anomalyzer.dtos.AnomaliaDTO;
import org.itson.Anomalyzer.dtos.AnomaliaResponseDTO;
import org.itson.Anomalyzer.encriptadores.EncriptadorRSA;
import org.itson.Anomalyzer.persistence.anomalias.IAnomaliasRepository;
import org.itson.Anomalyzer.persistence.alarmas.IAlarmasRepository;
import org.itson.Anomalyzer.proto.ClienteAlarmasGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AnomalyzerService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ClienteAlarmasGrpc clienteAlarmasGrpc;

    @Autowired
    IAnomaliasRepository anomaliasRepository;

    @Autowired
    IAlarmasRepository alarmasRepository;

    private static final String QUEUE_ALARMAS = "alarmas";

    private final Gson gson = new Gson();

    private Channel channel;

    @PostConstruct
    private void init() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (Exception e) {
            System.err.println("Error al crear la cola: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void guardarAnomalia(AnomaliaDTO anomaliaDTO) {
        Anomalia anomalia = convertirAnomalia(anomaliaDTO);
        anomaliasRepository.save(anomalia);
        System.out.println("Anomalia guardada con éxito en la base de datos.");
    }

    private Anomalia convertirAnomalia(AnomaliaDTO anomaliaDTO) {
        Anomalia anomalia = new Anomalia(
                anomaliaDTO.getIdSensor(),
                anomaliaDTO.getMacAddress(),
                anomaliaDTO.getMarca(),
                anomaliaDTO.getModelo(),
                anomaliaDTO.getMagnitud(),
                anomaliaDTO.getUnidad(),
                anomaliaDTO.getValor(),
                anomaliaDTO.getFechaHora(),
                anomaliaDTO.getIdInvernadero(),
                anomaliaDTO.getNombreInvernadero(),
                anomaliaDTO.getSector(),
                anomaliaDTO.getFila(),
                anomaliaDTO.getCausa()
        );
        return anomalia;
    }

    public void enviarNotificacion(AlarmaDTO alarmaDetonadora, AnomaliaDTO anomalia) {
        try {
            AlarmaAnomaliaDTO alarmaAnomalia = new AlarmaAnomaliaDTO(alarmaDetonadora, anomalia);
            String json = gson.toJson(alarmaAnomalia);

            PublicKey llavePublica = EncriptadorRSA.loadPublicKey("src/main/resources/keys/clave_publica_alarmator.pem");

            String jsonEncriptado = EncriptadorRSA.encryptHybrid(json, llavePublica);

            channel.queueDeclare(QUEUE_ALARMAS, false, false, false, null);
            channel.basicPublish("", QUEUE_ALARMAS, null, jsonEncriptado.getBytes());
        } catch (IOException e) {
            System.out.println("Error al enviar la anomalía: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void desactivarAlarma(AlarmaDTO alarmaDetonadora) {
        clienteAlarmasGrpc.desactivarAlarma(alarmaDetonadora);
    }

    public List<AnomaliaResponseDTO> obtenerAnomaliasPorFechas(String fechaInicioStr, String fechaFinStr) {
        // Convertir strings de fecha a Date (el formato debe coincidir con lo que envía el frontend)
        // El frontend envía YYYY-MM-DD. Necesitamos construir el rango del día completo.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaInicio;
        Date fechaFin;
        try {
            Date inicio = sdf.parse(fechaInicioStr);
            // Para el fin, tomamos el final del día
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(fechaFinStr));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            fechaFin = cal.getTime();
            fechaInicio = inicio; // Inicio del día ya está bien
        } catch (ParseException e) {
            System.err.println("Error al parsear fechas: " + e.getMessage());
            // Considerar lanzar una excepción o devolver lista vacía con error
            return Collections.emptyList();
        }

        System.out.println("[AnomalyzerService INFO] Buscando anomalías entre " + fechaInicio + " y " + fechaFin);
        List<Anomalia> anomalias = anomaliasRepository.findByFechaHoraBetween(fechaInicio, fechaFin);
        if (anomalias.isEmpty()) {
            System.out.println("[AnomalyzerService INFO] No se encontraron anomalías para el rango de fechas.");
            return Collections.emptyList();
        }

        List<AnomaliaResponseDTO> dtos = new ArrayList<>();
        for (Anomalia anomalia : anomalias) {
            // TODO: Determinar si la anomalía tiene reporte.
            // Esto podría requerir una llamada a ReportesAnomaliasService
            // o si ReportesAnomalias guarda el idAnomalia en su reporte.
            // Por ahora, lo dejaremos como 'false'.
            boolean tieneReporte = verificarSiAnomaliaTieneReporte(anomalia.get_id().toString());
            // Necesitamos implementar esta lógica

            dtos.add(convertirAnomaliaAAnomaliaResponseDTO(anomalia, tieneReporte));
        }
        System.out.println("[AnomalyzerService INFO] Encontradas " + dtos.size() + " anomalías.");
        return dtos;
    }

    // Método helper para convertir Anomalia (entidad) a AnomaliaResponseDTO
    private AnomaliaResponseDTO convertirAnomaliaAAnomaliaResponseDTO(Anomalia anomalia, boolean tieneReporte) {
        return new AnomaliaResponseDTO(
                anomalia.get_id().toString(),
                anomalia.getIdSensor(),
                anomalia.getMacAddress(),
                anomalia.getMarca(),
                anomalia.getModelo(),
                anomalia.getMagnitud(),
                anomalia.getUnidad(),
                anomalia.getValor(),
                anomalia.getFechaHora(),
                anomalia.getIdInvernadero(),
                anomalia.getNombreInvernadero(),
                anomalia.getSector(),
                anomalia.getFila(),
                anomalia.getCausa(),
                tieneReporte
        );
    }

    public boolean verificarSiAnomaliaTieneReporte(String anomaliaId) {
        String url = "http://localhost:8081/api/v1/reportesAnomalias/existe?anomaliaId=" + anomaliaId;
        try {
            Map<String, Boolean> response = restTemplate.getForObject(url, Map.class);
            return response != null && Boolean.TRUE.equals(response.get("existe"));
        } catch (Exception e) {
            System.err.println("Error consultando ReportesAnomalias: " + e.getMessage());
            return false;
        }
    }
}
