package org.itson.Alarmas.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "alarmas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alarma {
    @Id
    private ObjectId _id;
    private String idAlarma;
    private String magnitud;
    private List<ObjectId> sensores;
    private String invernadero;
    private float valorMinimo;
    private float valorMaximo;
    private String unidad;
    private String medioNotificacion;
    private boolean activo;

    public Alarma(String idAlarma, String magnitud, List<ObjectId> sensores, String invernadero, float valorMinimo, float valorMaximo, String unidad, String medioNotificacion, boolean activo) {
        this.idAlarma = idAlarma;
        this.magnitud = magnitud;
        this.sensores = sensores;
        this.invernadero = invernadero;
        this.valorMinimo = valorMinimo;
        this.valorMaximo = valorMaximo;
        this.unidad = unidad;
        this.medioNotificacion = medioNotificacion;
        this.activo = activo;
    }
}
