package org.itson.GestionSensores.persistence;

import org.bson.types.ObjectId;
import org.itson.GestionSensores.collections.Invernadero;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interfaz que define los métodos de acceso a datos disponibles para interactuar con la tabla de invernaderos de la base de datos. Como hereda de JpaRepository, ya cuenta con métodos predefinidos (findAll, findById, deleteById...)
 */
@Repository
public interface IInvernaderosRepository extends MongoRepository<Invernadero, ObjectId> {
}
