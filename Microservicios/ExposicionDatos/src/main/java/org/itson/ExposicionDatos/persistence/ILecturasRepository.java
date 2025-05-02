package org.itson.ExposicionDatos.persistence;

import org.bson.types.ObjectId;
import org.itson.ExposicionDatos.collections.Lectura;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILecturasRepository extends MongoRepository<Lectura, ObjectId> {
}
