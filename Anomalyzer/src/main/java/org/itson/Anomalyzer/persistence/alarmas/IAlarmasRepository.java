package org.itson.Anomalyzer.persistence.alarmas;

import org.bson.types.ObjectId;
import org.itson.Anomalyzer.collections.Alarma;
import org.itson.Anomalyzer.collections.Anomalia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAlarmasRepository extends MongoRepository<Alarma, ObjectId> {
}
