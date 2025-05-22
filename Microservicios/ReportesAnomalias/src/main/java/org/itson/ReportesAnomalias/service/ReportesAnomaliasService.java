package org.itson.ReportesAnomalias.service;

import org.bson.types.ObjectId;
import org.itson.ReportesAnomalias.collections.Anomalia;
import org.itson.ReportesAnomalias.collections.ReporteAnomalia;
import org.itson.ReportesAnomalias.dtos.*;
import org.itson.ReportesAnomalias.excepciones.ReportesAnomaliasServiceException;
import org.itson.ReportesAnomalias.persistence.anomalias.IAnomaliasRepository;
import org.itson.ReportesAnomalias.persistence.reportes.IReportesAnomaliasRepository;
import org.itson.ReportesAnomalias.proto.ClienteGestionSensoresGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReportesAnomaliasService {
    @Autowired
    private IAnomaliasRepository anomaliasRepository;

    @Autowired
    private IReportesAnomaliasRepository reportesAnomaliasRepository;

    @Autowired
    private ClienteGestionSensoresGrpc clienteGestionSensores;

    public List<AnomaliaDTO> obtenerAnomalias() throws ReportesAnomaliasServiceException {
        List<Anomalia> anomaliasEncontradas = anomaliasRepository.findAll();
        if (!anomaliasEncontradas.isEmpty()) {
            // Si se obtuvo algo, se convierte a DTO y se regresa eso.
            List<AnomaliaDTO> anomalias = new LinkedList<>();
            List<String> idSensores = new ArrayList<>();
            for (Anomalia anomalia : anomaliasEncontradas) {
                anomalias.add(convertirAnomalia(anomalia));
                idSensores.add(anomalia.getIdSensor());
            }

            enriquecerAnomalias(anomalias, idSensores);

            return anomalias;
        } else {
            throw new ReportesAnomaliasServiceException("No se encontraron anomalías.");
        }
    }

    private List<AnomaliaDTO> enriquecerAnomalias(List<AnomaliaDTO> anomalias, List<String> idSensores) {
        List<DatosFaltantesDTO> listaDatosFaltantes = clienteGestionSensores.obtenerDatosFaltantes(idSensores);
        for (AnomaliaDTO anomalia : anomalias) {
            for (DatosFaltantesDTO datosFaltantes : listaDatosFaltantes) {
                if (anomalia.getIdSensor().equals(datosFaltantes.getIdSensor())) {
                    anomalia.setSector(datosFaltantes.getSector());
                    anomalia.setFila(datosFaltantes.getFila());
                    break;
                }
            }
        }
        return anomalias;
    }

    public List<AnomaliaDTO> obtenerAnomaliasPorPeriodo(Date fechaIncio, Date fechaFin) throws ReportesAnomaliasServiceException {
        List<Anomalia> anomaliasEncontradas = anomaliasRepository.findAllByFechaHoraBetween(fechaIncio, fechaFin);
        if (!anomaliasEncontradas.isEmpty()) {
            // Si se obtuvo algo, se convierte a DTO y se regresa eso.
            List<AnomaliaDTO> anomalias = new LinkedList<>();
            List<String> idSensores = new ArrayList<>();
            for (Anomalia anomalia : anomaliasEncontradas) {
                anomalias.add(convertirAnomalia(anomalia));
                idSensores.add(anomalia.getIdSensor());
            }

            enriquecerAnomalias(anomalias, idSensores);

            return anomalias;
        } else {
            throw new ReportesAnomaliasServiceException("Anomalias entre el periodo: " + fechaIncio + " - " + fechaFin + " no encontradas");
        }
    }

    public List<AnomaliaDTO> obtenerAnomaliasPorInvernadero(String id) throws ReportesAnomaliasServiceException {
        List<Anomalia> anomaliasEncontradas = anomaliasRepository.findAllByIdInvernadero(new ObjectId(id));
        if (!anomaliasEncontradas.isEmpty()) {
            // Si se obtuvo algo, se convierte a DTO y se regresa eso.
            List<AnomaliaDTO> anomalias = new LinkedList<>();
            List<String> idSensores = new ArrayList<>();
            for (Anomalia anomalia : anomaliasEncontradas) {
                anomalias.add(convertirAnomalia(anomalia));
                idSensores.add(anomalia.getIdSensor());
            }

            enriquecerAnomalias(anomalias, idSensores);

            return anomalias;
        } else {
            throw new ReportesAnomaliasServiceException("Anomalias del invernadero con ID: " + id + " no encontradas");
        }
    }

    public List<AnomaliaDTO> obtenerAnomaliasPorSensor(String id) throws ReportesAnomaliasServiceException {
        List<Anomalia> anomaliasEncontradas = anomaliasRepository.findAllByIdSensor(id);
        if (!anomaliasEncontradas.isEmpty()) {
            // Si se obtuvo algo, se convierte a DTO y se regresa eso.
            List<AnomaliaDTO> anomalias = new LinkedList<>();
            List<String> idSensores = new ArrayList<>();
            for (Anomalia anomalia : anomaliasEncontradas) {
                anomalias.add(convertirAnomalia(anomalia));
                idSensores.add(anomalia.getIdSensor());
            }

            enriquecerAnomalias(anomalias, idSensores);

            return anomalias;
        } else {
            throw new ReportesAnomaliasServiceException("Anomalias del sensor con ID: " + id + " no encontradas");
        }
    }

    public List<AnomaliaDTO> obtenerAnomaliasPorMagnitud(String magnitud) throws ReportesAnomaliasServiceException {
        List<Anomalia> anomaliasEncontradas = anomaliasRepository.findAllByMagnitud(magnitud);
        if (!anomaliasEncontradas.isEmpty()) {
            // Si se obtuvo algo, se convierte a DTO y se regresa eso.
            List<AnomaliaDTO> anomalias = new LinkedList<>();
            List<String> idSensores = new ArrayList<>();
            for (Anomalia anomalia : anomaliasEncontradas) {
                anomalias.add(convertirAnomalia(anomalia));
                idSensores.add(anomalia.getIdSensor());
            }

            enriquecerAnomalias(anomalias, idSensores);

            return anomalias;
        } else {
            throw new ReportesAnomaliasServiceException("Anomalias con la magnitud: " + magnitud + " no encontradas");
        }
    }

    public ReporteAnomaliaDTO obtenerReporte(AnomaliaDTO anomalia) throws ReportesAnomaliasServiceException {
        ReporteAnomalia reporteEncontrado = reportesAnomaliasRepository.findByAnomalia(convertirAnomaliaDTO(anomalia));
        if (reporteEncontrado != null) {
            // Si se obtuvo algo, se convierte a DTO y se regresa eso.
            ReporteAnomaliaDTO reporte = convertirReporteAnomalia(reporteEncontrado);
            return reporte;
        } else {
            throw new ReportesAnomaliasServiceException("Reporte para la anomalía no encontrado.");
        }
    }

    public ReporteAnomaliaDTO registrarReporte(ReporteAnomaliaDTO reporte) throws ReportesAnomaliasServiceException {
        try {
            obtenerReporte(reporte.getAnomalia());
            throw new ReportesAnomaliasServiceException("Ya se ha registrado el reporte anteriormente.");
        } catch (ReportesAnomaliasServiceException e) {
            ReporteAnomalia reporteNuevo = convertirReporteAnomaliaDTO(reporte);
            ReporteAnomalia resultado = reportesAnomaliasRepository.save(reporteNuevo);
            return convertirReporteAnomalia(resultado);
        }
    }

    /**
     * _____ ____  _   ___      ________ _____   _____  ____  _____  ______  _____
     * / ____/ __ \| \ | \ \    / /  ____|  __ \ / ____|/ __ \|  __ \|  ____|/ ____|
     * | |   | |  | |  \| |\ \  / /| |__  | |__) | (___ | |  | | |__) | |__  | (___
     * | |   | |  | | . ` | \ \/ / |  __| |  _  / \___ \| |  | |  _  /|  __|  \___ \
     * | |___| |__| | |\  |  \  /  | |____| | \ \ ____) | |__| | | \ \| |____ ____) |
     * \______\____/|_| \_|   \/   |______|_|  \_\_____/ \____/|_|  \_\______|_____/
     */

    private Anomalia convertirAnomaliaDTO(AnomaliaDTO anomaliaDTO) {
        Anomalia anomaliaCreada = new Anomalia(
                anomaliaDTO.getIdSensor(), anomaliaDTO.getMacAddress(), anomaliaDTO.getMarca(),
                anomaliaDTO.getModelo(), anomaliaDTO.getMagnitud(), anomaliaDTO.getUnidad(), anomaliaDTO.getValor(),
                anomaliaDTO.getFechaHora(), anomaliaDTO.getIdInvernadero(), anomaliaDTO.getNombreInvernadero(),
                anomaliaDTO.getSector(), anomaliaDTO.getFila(), anomaliaDTO.getCausa());
        if (anomaliaDTO.get_id() != null && ObjectId.isValid(anomaliaDTO.get_id())) {
            anomaliaCreada.set_id(new ObjectId(anomaliaDTO.get_id()));
        }
        return anomaliaCreada;
    }

    private AnomaliaDTO convertirAnomalia(Anomalia anomalia) {
        AnomaliaDTO anomaliaCreada = new AnomaliaDTO(
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
                anomalia.getCausa());

        return anomaliaCreada;
    }

    private ReporteAnomalia convertirReporteAnomaliaDTO(ReporteAnomaliaDTO reporte) {
        ReporteAnomalia reporteCreado = new ReporteAnomalia(reporte.getFecha(), reporte.getAcciones(), reporte.getComentarios(), convertirAnomaliaDTO(reporte.getAnomalia()), reporte.getUsuario());

        return reporteCreado;
    }

    private ReporteAnomaliaDTO convertirReporteAnomalia(ReporteAnomalia reporte) {
        ReporteAnomaliaDTO reporteCreado = new ReporteAnomaliaDTO();

        reporteCreado.setId(reporte.get_id().toString());
        reporteCreado.setFecha(reporte.getFecha());
        reporteCreado.setAcciones(reporte.getAcciones());
        reporteCreado.setComentarios(reporte.getComentarios());
        reporteCreado.setAnomalia(convertirAnomalia(reporte.getAnomalia()));
        reporteCreado.setUsuario(reporte.getUsuario());

        return reporteCreado;
    }

    public boolean existeReporteParaAnomalia(String anomaliaId) {
        return reportesAnomaliasRepository.existsByAnomalia__id(new ObjectId(anomaliaId));
    }

    public ReporteAnomaliaDTO obtenerReportePorAnomaliaId(String anomaliaId) throws ReportesAnomaliasServiceException {
        if (!ObjectId.isValid(anomaliaId)) {
            throw new ReportesAnomaliasServiceException("ID de anomalía inválido: " + anomaliaId);
        }

        // Primero verificamos si la anomalía existe
        Optional<Anomalia> anomaliaOpt = anomaliasRepository.findById(new ObjectId(anomaliaId));
        if (!anomaliaOpt.isPresent()) {
            throw new ReportesAnomaliasServiceException("No existe anomalía con ID: " + anomaliaId);
        }

        // Buscar el reporte por ID de anomalía - Utilizando el método correcto con dos guiones bajos
        ReporteAnomalia reporteEncontrado = reportesAnomaliasRepository.findByAnomalia__id(new ObjectId(anomaliaId));
        if (reporteEncontrado == null) {
            throw new ReportesAnomaliasServiceException("No existe un reporte para la anomalía con ID: " + anomaliaId);
        }

        // Convertir y retornar el reporte
        return convertirReporteAnomalia(reporteEncontrado);
    }

    public AnomaliaDTO obtenerAnomaliaPorId(String anomaliaObjectId) throws ReportesAnomaliasServiceException {
        if (!ObjectId.isValid(anomaliaObjectId)) {
            throw new ReportesAnomaliasServiceException("ID de anomalía inválido: " + anomaliaObjectId);
        }
        Optional<Anomalia> anomaliaOpt = anomaliasRepository.findById(new ObjectId(anomaliaObjectId));
        if (anomaliaOpt.isPresent()) {
            Anomalia anomalia = anomaliaOpt.get();
            return convertirAnomalia(anomalia);
        } else {
            throw new ReportesAnomaliasServiceException("Anomalía con ID: " + anomaliaObjectId + " no encontrada en el servicio de reportes.");
        }
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
        try {
            return reportesAnomaliasRepository.existsByAnomalia__id(anomaliaId);
        } catch (Exception e) {
            System.err.println("Error consultando ReportesAnomalias en la base de datos: " + e.getMessage());
            return false;
        }
    }
}
