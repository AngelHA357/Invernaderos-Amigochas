package org.itson.ReportesAnomalias.persistence.reportes;

import org.bson.types.ObjectId;
import org.itson.ReportesAnomalias.collections.Anomalia;
import org.itson.ReportesAnomalias.collections.ReporteAnomalia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReportesAnomaliasRepository extends MongoRepository<ReporteAnomalia, ObjectId> {

    public ReporteAnomalia findByAnomalia(Anomalia anomalia);

    boolean existsByAnomalia__id(ObjectId anomaliaId);

    /**
     * Busca un reporte por el ID de la anomalía asociada
     * @param anomaliaId ID de la anomalía a buscar
     * @return El reporte encontrado o null si no existe
     */
    ReporteAnomalia findByAnomalia__id(ObjectId anomaliaId);

}
