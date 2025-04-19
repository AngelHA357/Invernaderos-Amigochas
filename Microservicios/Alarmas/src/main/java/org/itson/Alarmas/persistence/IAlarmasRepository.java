package org.itson.Alarmas.persistence;

import org.itson.Alarmas.collections.Alarma;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAlarmasRepository extends MongoRepository<Alarma, ObjectId> {
    Optional<Alarma> findByIdAlarma(String idAlarma);
}
