package org.itson.ReportesAnomalias.persistence;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.itson.ReportesAnomalias.conexion.IConexion;
import org.itson.ReportesAnomalias.entities.Anomalia;
import org.itson.ReportesAnomalias.entities.ReporteAnomalia;
import org.itson.ReportesAnomalias.excepciones.ReportesAnomaliasPersistenceException;
import static com.mongodb.client.model.Filters.eq;

public class ReportesAnomaliasRepository implements IReportesAnomaliasRepository {

    private final MongoDatabase baseDatos;
    private final String nombreColeccion;

    public ReportesAnomaliasRepository(IConexion conexion) {
        baseDatos = conexion.crearConexion();
        nombreColeccion = "reportes_anomalias";
    }

    @Override
    public ReporteAnomalia obtenerReporte(Anomalia anomalia) {
        MongoCollection<ReporteAnomalia> coleccion = baseDatos.getCollection(nombreColeccion, ReporteAnomalia.class);

        ReporteAnomalia reporte = coleccion.find(eq("anomalia", anomalia)).first();

        return reporte;
    }

    @Override
    public ReporteAnomalia registrarReporte(ReporteAnomalia reporte) throws ReportesAnomaliasPersistenceException  {
        if (obtenerReporte(reporte.getAnomalia()) == null) {
            MongoCollection<ReporteAnomalia> coleccion = baseDatos.getCollection(nombreColeccion, ReporteAnomalia.class);

            coleccion.insertOne(reporte);

            return reporte;
        } else {
            throw new ReportesAnomaliasPersistenceException("El reporte ya se ha registrado enteriormente.");
        }
    }

}
