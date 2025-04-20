package org.itson.Anomalyzer.persistence;

import org.bson.types.ObjectId;
import org.itson.Anomalyzer.collections.Anomalia;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IAnomaliasRepository extends MongoRepository<Anomalia, ObjectId> {
}
