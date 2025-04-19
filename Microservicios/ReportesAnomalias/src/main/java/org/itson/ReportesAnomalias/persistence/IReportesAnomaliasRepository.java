package org.itson.ReportesAnomalias.persistence;

import org.bson.types.ObjectId;
import org.itson.ReportesAnomalias.collections.Anomalia;
import org.itson.ReportesAnomalias.collections.ReporteAnomalia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReportesAnomaliasRepository extends MongoRepository<ReporteAnomalia, ObjectId> {

    public ReporteAnomalia findByAnomalia(Anomalia anomalia);

}
