package org.itson.Anomalyzer.persistence.anomalias;

import org.bson.types.ObjectId;
import org.itson.Anomalyzer.collections.Anomalia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAnomaliasRepository extends MongoRepository<Anomalia, ObjectId> {
}
