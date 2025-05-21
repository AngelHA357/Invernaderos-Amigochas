package org.itson.Informes.collections;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LecturaCruda {

    @Id
    private ObjectId _id;

    private String idSensor;
    private String macAddress;
    private String marca;
    private String modelo;
    private String magnitud;
    private String unidad;
    private float valor;
    private Date fechaHora;
    private String idInvernadero;
    private String nombreInvernadero;
    private String sector;
    private String fila;

}
