/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.itson.dtos;

/**
 *
 * @author Equipo 1
 */
public class LecturaDTO {

    private String idSensor;
    private String tipoSensor;
    private double valor;
    private String timestamp;

    public LecturaDTO(String idSensor, String tipoSensor, double medicion, String timestamp) {
        this.idSensor = idSensor;
        this.tipoSensor = tipoSensor;
        this.valor = medicion;
        this.timestamp = timestamp;
    }

    public String getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(String idSensor) {
        this.idSensor = idSensor;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public void setTipoSensor(String tipoSensor) {
        this.tipoSensor = tipoSensor;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double medicion) {
        this.valor = medicion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
