package org.itson.Informes.persistence;

import org.bson.types.ObjectId;
import org.itson.Informes.collections.InformeLectura;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IInformeLecturasRepository extends MongoRepository<InformeLectura, ObjectId> {

    @Query("{ 'idInvernadero': ?0, 'fechaHora': { $gte: ?1, $lte: ?2 }, 'magnitud': ?3 }")
    List<InformeLectura> findInformeLecturasByFiltros(String idInvernadero, Date fechaInicio, Date fechaFin, String magnitud);
}
