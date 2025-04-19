package org.itson.Alarmas.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@NoArgsConstructor
public class AlarmaDTO {
    private String idAlarma;
    private String magnitud;
    private List<ObjectId> sensores;
    private String invernadero;
    private float valorMinimo;
    private float valorMaximo;
    private float unidad;
    private String medioNotificacion;
    private boolean activo;

    public AlarmaDTO(String idAlarma, String magnitud, List<ObjectId> sensores, String invernadero, float valorMinimo, float valorMaximo, float unidad, String medioNotificacion, boolean activo) {
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
