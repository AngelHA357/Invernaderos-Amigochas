package org.itson.Alarmas.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmaDTO {
    private String idAlarma;
    private String magnitud;
    private List<String> sensores;
    private String invernadero;
    private float valorMinimo;
    private float valorMaximo;
    private String unidad;
    private String medioNotificacion;
    private boolean activo;
}
