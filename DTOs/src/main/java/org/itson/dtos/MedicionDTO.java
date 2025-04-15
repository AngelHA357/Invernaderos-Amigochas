/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.itson.dtos;

/**
 *
 * @author Equipo 1
 */
public class MedicionDTO {

    private String idSensor;
    private String tipoSensor;
    private double medicion;
    private String timestamp;

    public MedicionDTO(String idSensor, String tipoSensor, double medicion, String timestamp) {
        this.idSensor = idSensor;
        this.tipoSensor = tipoSensor;
        this.medicion = medicion;
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

    public double getMedicion() {
        return medicion;
    }

    public void setMedicion(double medicion) {
        this.medicion = medicion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
