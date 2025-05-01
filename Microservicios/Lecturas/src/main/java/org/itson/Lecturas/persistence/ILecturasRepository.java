package org.itson.Lecturas.persistence;

import org.itson.Lecturas.collections.Lectura;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;

@Repository
public interface ILecturasRepository extends MongoRepository<Lectura, org.bson.types.ObjectId> {
    @Query("{ 'idInvernadero': ?0, 'fechaHora': { $gte: ?1, $lte: ?2 }, 'magnitud': ?3 }")
    List<Lectura> findLecturasByFiltros(String idInvernadero, Date fechaInicio, Date fechaFin, String magnitud);
}
