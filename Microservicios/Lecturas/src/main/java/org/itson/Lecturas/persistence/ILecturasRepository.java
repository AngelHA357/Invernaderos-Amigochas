package org.itson.Lecturas.persistence;

import org.itson.Lecturas.entities.Lectura;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILecturasRepository extends MongoRepository<Lectura, Long> {

}
