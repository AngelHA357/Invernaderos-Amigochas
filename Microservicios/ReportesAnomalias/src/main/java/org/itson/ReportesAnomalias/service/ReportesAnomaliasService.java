package org.itson.ReportesAnomalias.service;

import org.bson.types.ObjectId;
import org.itson.ReportesAnomalias.dtos.AnomaliaDTO;
import org.itson.ReportesAnomalias.dtos.ReporteAnomaliaDTO;
import org.itson.ReportesAnomalias.entities.Anomalia;
import org.itson.ReportesAnomalias.entities.ReporteAnomalia;
import org.itson.ReportesAnomalias.excepciones.AnomaliasPersistenceException;
import org.itson.ReportesAnomalias.excepciones.ReportesAnomaliasPersistenceException;
import org.itson.ReportesAnomalias.excepciones.ReportesAnomaliasServiceException;
import org.itson.ReportesAnomalias.persistence.IAnomaliasRepository;
import org.itson.ReportesAnomalias.persistence.IReportesAnomaliasRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;

public class ReportesAnomaliasService {

    @Autowired
    private IAnomaliasRepository anomaliasRepository;

    @Autowired
    private IReportesAnomaliasRepository reportesAnomaliasRepository;

    public AnomaliaDTO obtenerAnomalia(AnomaliaDTO anomalia) {
        Anomalia anomaliaEncontrada = anomaliasRepository.obtenerAnomalia(convertirAnomaliaDTO(anomalia));

        return convertirAnomalia(anomaliaEncontrada);
    }

    public AnomaliaDTO obtenerAnomaliasPorPeriodo(Calendar fechaIncio, Calendar fechaFin) {
        Anomalia anomaliaEncontrada = anomaliasRepository.obtenerAnomaliasPorPeriodo(fechaIncio, fechaFin);

        return convertirAnomalia(anomaliaEncontrada);
    }

    public AnomaliaDTO obtenerAnomaliasPorInvernadero(String idInvernadero) {
        Anomalia anomaliaEncontrada = anomaliasRepository.obtenerAnomaliasPorInvernadero(idInvernadero);

        return convertirAnomalia(anomaliaEncontrada);
    }

    public AnomaliaDTO obtenerAnomaliasPorSensor(String idSensor) {
        Anomalia anomaliaEncontrada = anomaliasRepository.obtenerAnomaliasPorSensor(idSensor);

        return convertirAnomalia(anomaliaEncontrada);
    }

    public AnomaliaDTO obtnerAnomaliasPorMagnitud(String magnitud) {
        Anomalia anomaliaEncontrada = anomaliasRepository.obtenerAnomaliasPorMagnitud(magnitud);

        return convertirAnomalia(anomaliaEncontrada);
    }

    public AnomaliaDTO registrarAnomalia(AnomaliaDTO anomalia) throws ReportesAnomaliasServiceException {
        try {
            Anomalia anomaliaNueva = convertirAnomaliaDTO(anomalia);

            return convertirAnomalia(anomaliasRepository.registrarAnomalia(anomaliaNueva));
        } catch (AnomaliasPersistenceException e) {
            throw new ReportesAnomaliasServiceException(e.getMessage());
        }
    }

    public ReporteAnomaliaDTO obtenerReporte(AnomaliaDTO anomalia) {
        ReporteAnomalia reporteEncontrado = reportesAnomaliasRepository.obtenerReporte(convertirAnomaliaDTO(anomalia));

        return convertirReporteAnomalia(reporteEncontrado);
    }

    public ReporteAnomaliaDTO registrarReporte(ReporteAnomaliaDTO reporte) throws ReportesAnomaliasServiceException {
        try {
            ReporteAnomalia reporteNuevo = convertirReporteAnomaliaDTO(reporte);

            return convertirReporteAnomalia(reportesAnomaliasRepository.registrarReporte(reporteNuevo));
        } catch (ReportesAnomaliasPersistenceException e) {
            throw new ReportesAnomaliasServiceException(e.getMessage());
        }
    }

    /**
     *    _____ ____  _   ___      ________ _____   _____  ____  _____  ______  _____
     *   / ____/ __ \| \ | \ \    / /  ____|  __ \ / ____|/ __ \|  __ \|  ____|/ ____|
     *  | |   | |  | |  \| |\ \  / /| |__  | |__) | (___ | |  | | |__) | |__  | (___
     *  | |   | |  | | . ` | \ \/ / |  __| |  _  / \___ \| |  | |  _  /|  __|  \___ \
     *  | |___| |__| | |\  |  \  /  | |____| | \ \ ____) | |__| | | \ \| |____ ____) |
     *  \______\____/|_| \_|   \/   |______|_|  \_\_____/ \____/|_|  \_\______|_____/
     */

    private Anomalia convertirAnomaliaDTO(AnomaliaDTO anomalia) {
        Anomalia anomaliaCreada = new Anomalia(anomalia.getCodigo(), anomalia.getFechaHora(), anomalia.getCausa(), anomalia.getInvernadero(), anomalia.getSensor());

        return anomaliaCreada;
    }

    private AnomaliaDTO convertirAnomalia(Anomalia anomalia) {
        AnomaliaDTO anomaliaCreada = new AnomaliaDTO();

        anomaliaCreada.setCodigo(anomalia.getId().toString());
        anomaliaCreada.setFechaHora(anomalia.getFechaHora());
        anomaliaCreada.setCausa(anomalia.getCausa());
        anomaliaCreada.setInvernadero(anomalia.getInvernadero());
        anomaliaCreada.setSensor(anomalia.getSensor());

        return anomaliaCreada;
    }

    private ReporteAnomalia convertirReporteAnomaliaDTO(ReporteAnomaliaDTO reporte) {
        ReporteAnomalia reporteCreado = new ReporteAnomalia(reporte.getCodigo(), reporte.getFecha(), reporte.getDescripcion(), reporte.getAnomalia(), reporte.getUsuario());

        return reporteCreado;
    }

    private ReporteAnomaliaDTO convertirReporteAnomalia(ReporteAnomalia reporte) {
        ReporteAnomaliaDTO reporteCreado = new ReporteAnomaliaDTO();

        reporteCreado.setCodigo(reporte.getCodigo());
        reporteCreado.setFecha(reporte.getFecha());
        reporteCreado.setDescripcion(reporte.getDescripcion());
        reporteCreado.setAnomalia(convertirAnomalia(reporte.getAnomalia()));
        reporteCreado.setUsuario(reporte.getUsuario());

        return reporteCreado;
    }


}
