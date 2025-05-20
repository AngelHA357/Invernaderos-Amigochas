package org.itson.Informes.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.itson.Informes.dtos.LecturaDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "lecturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformeLectura {

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

    public InformeLectura(LecturaDTO dto) {

        if (dto.get_id() != null && ObjectId.isValid(dto.get_id())) {
            this._id = new ObjectId(dto.get_id());
        } else {
            this._id = new ObjectId();
        }
        this.idSensor = dto.getIdSensor();
        this.macAddress = dto.getMacAddress();
        this.marca = dto.getMarca();
        this.modelo = dto.getModelo();
        this.magnitud = dto.getMagnitud();
        this.unidad = dto.getUnidad();
        this.valor = dto.getValor();
        this.fechaHora = dto.getFechaHora();
        this.idInvernadero = dto.getIdInvernadero();
        this.nombreInvernadero = dto.getNombreInvernadero();
        this.sector = dto.getSector();
        this.fila = dto.getFila();
    }
}
