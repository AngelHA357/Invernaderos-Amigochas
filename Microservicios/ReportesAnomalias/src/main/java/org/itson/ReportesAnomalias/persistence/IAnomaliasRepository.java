package org.itson.ReportesAnomalias.persistence;

import org.bson.types.ObjectId;
import org.itson.ReportesAnomalias.entities.Anomalia;
import org.itson.ReportesAnomalias.entities.ReporteAnomalia;
import org.itson.ReportesAnomalias.excepciones.AnomaliasPersistenceException;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public interface IAnomaliasRepository extends MongoRepository<Anomalia, ObjectId> {
    public List<Anomalia> findAllByFechaHoraBetween(Date fechaInicio, Date fechaFin);

    public List<Anomalia> findAllByInvernadero(String id);

    public List<Anomalia> findAllBySensor(String id);

    public List<Anomalia> findAllByMagn(String magnitud);
}
