package org.itson.ReportesAnomalias.persistence;

import org.itson.ReportesAnomalias.entities.Anomalia;
import org.itson.ReportesAnomalias.entities.ReporteAnomalia;
import org.itson.ReportesAnomalias.excepciones.AnomaliasPersistenceException;

import java.util.Calendar;
import java.util.List;

public interface IAnomaliasRepository {

    public Anomalia obtenerAnomalia(Anomalia anomalia);

    public List<Anomalia> obtenerAnomaliasPorPeriodo(Calendar fechaInicio, Calendar fechaFin);

    public List<Anomalia> obtenerAnomaliasPorInvernadero(String id);

    public List<Anomalia> obtenerAnomaliasPorSensor(String id);

    public List<Anomalia> obtenerAnomaliasPorMagnitud(String magnitud);

    public Anomalia registrarAnomalia(Anomalia anomalia) throws AnomaliasPersistenceException;
}
