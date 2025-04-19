package org.itson.ReportesAnomalias.persistence;

import org.itson.ReportesAnomalias.entities.Anomalia;
import org.itson.ReportesAnomalias.entities.ReporteAnomalia;
import org.itson.ReportesAnomalias.excepciones.ReportesAnomaliasPersistenceException;

import java.util.Calendar;
import java.util.List;

public interface IReportesAnomaliasRepository {

    public ReporteAnomalia obtenerReporte(Anomalia anomalia);

    public ReporteAnomalia registrarReporte(ReporteAnomalia reporte) throws ReportesAnomaliasPersistenceException;

}
