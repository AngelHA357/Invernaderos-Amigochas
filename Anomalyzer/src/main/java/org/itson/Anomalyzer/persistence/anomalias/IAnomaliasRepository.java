package org.itson.Anomalyzer.persistence.anomalias;

import org.bson.types.ObjectId;
import org.itson.Anomalyzer.collections.Anomalia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IAnomaliasRepository extends MongoRepository<Anomalia, ObjectId> {
    List<Anomalia> findByFechaHoraBetween(Date fechaInicio, Date fechaFin);
}
