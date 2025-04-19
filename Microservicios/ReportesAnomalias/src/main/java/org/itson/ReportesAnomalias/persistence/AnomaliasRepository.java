package org.itson.ReportesAnomalias.persistence;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.itson.ReportesAnomalias.conexion.IConexion;
import org.itson.ReportesAnomalias.entities.Anomalia;
import org.itson.ReportesAnomalias.excepciones.AnomaliasPersistenceException;
import org.springframework.web.servlet.tags.form.HiddenInputTag;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class AnomaliasRepository implements IAnomaliasRepository {

    private final MongoDatabase baseDatos;
    private final String nombreColeccion;

    public AnomaliasRepository(IConexion conexion) {
        baseDatos = conexion.crearConexion();
        nombreColeccion = "anomalias";
    }

    @Override
    public Anomalia obtenerAnomalia(Anomalia anomalia) {
        MongoCollection<Anomalia> coleccion = baseDatos.getCollection(nombreColeccion, Anomalia.class);

        Anomalia anomaliaRegistrada = coleccion.find(eq("codigo", anomalia.getCodigo())).first();

        return anomaliaRegistrada;
    }

    @Override
    public List<Anomalia> obtenerAnomaliasPorPeriodo(Calendar fechaInicio, Calendar fechaFin) {
        MongoCollection<Anomalia> coleccion = baseDatos.getCollection(nombreColeccion, Anomalia.class);

        List<Anomalia> anomalias = new LinkedList<>();
        coleccion.find(and(gte("fecha", fechaInicio), lte("fecha", fechaFin))).into(anomalias);

        return anomalias;
    }

    @Override
    public List<Anomalia> obtenerAnomaliasPorInvernadero(String idInvernadero) {
        MongoCollection<Anomalia> coleccion = baseDatos.getCollection(nombreColeccion, Anomalia.class);

        List<Anomalia> anomalias = new LinkedList<>();
        coleccion.find(eq("invernadero", idInvernadero)).into(anomalias);

        return anomalias;
    }

    @Override
    public List<Anomalia> obtenerAnomaliasPorSensor(String idSensor) {
        MongoCollection<Anomalia> coleccion = baseDatos.getCollection(nombreColeccion, Anomalia.class);

        List<Anomalia> anomalias = new LinkedList<>();
        coleccion.find(eq("sensor", idSensor)).into(anomalias);

        return anomalias;
    }

    @Override
    public List<Anomalia> obtenerAnomaliasPorMagnitud(String magnitud) {
        MongoCollection<Anomalia> coleccion = baseDatos.getCollection(nombreColeccion, Anomalia.class);

        List<Anomalia> anomalias = new LinkedList<>();
        coleccion.find(eq("magnitud", magnitud)).into(anomalias);

        return anomalias;
    }

    @Override
    public Anomalia registrarAnomalia(Anomalia anomalia) throws AnomaliasPersistenceException {
        if(obtenerAnomalia(anomalia) == null) {
            MongoCollection<Anomalia> coleccion = baseDatos.getCollection(nombreColeccion, Anomalia.class);

            coleccion.insertOne(anomalia);

            return anomalia;
        } else {
            throw new AnomaliasPersistenceException("Esta anomalía ya ha sido registrada anteriormente.");
        }
    }
}
