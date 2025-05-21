package org.itson.ReportesAnomalias.service;

import org.bson.types.ObjectId;
import org.itson.ReportesAnomalias.collections.Anomalia;
import org.itson.ReportesAnomalias.collections.ReporteAnomalia;
import org.itson.ReportesAnomalias.dtos.AnomaliaDTO;
import org.itson.ReportesAnomalias.dtos.DatosFaltantesDTO;
import org.itson.ReportesAnomalias.dtos.LecturaDTO;
import org.itson.ReportesAnomalias.dtos.ReporteAnomaliaDTO;
import org.itson.ReportesAnomalias.excepciones.ReportesAnomaliasServiceException;
import org.itson.ReportesAnomalias.persistence.anomalias.IAnomaliasRepository;
import org.itson.ReportesAnomalias.persistence.reportes.IReportesAnomaliasRepository;
import org.itson.ReportesAnomalias.proto.ClienteGestionSensoresGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private Anomalia convertirAnomaliaDTO(AnomaliaDTO anomalia) {
        Anomalia anomaliaCreada = new Anomalia(anomalia.getIdSensor(), anomalia.getMacAddress(), anomalia.getMarca(),
                anomalia.getModelo(), anomalia.getMagnitud(), anomalia.getUnidad(), anomalia.getValor(),
                anomalia.getFechaHora(), anomalia.getIdInvernadero(), anomalia.getNombreInvernadero(),
                anomalia.getSector(), anomalia.getFila(), anomalia.getCausa());

        return anomaliaCreada;
    }

    private AnomaliaDTO convertirAnomalia(Anomalia anomalia) {
        AnomaliaDTO anomaliaCreada = new AnomaliaDTO(
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

}
