/*
 * DTOUtils.java
 */
package org.itson.simuladorsensores.utils;

import java.util.Date;

/**
 * Clase utilitaria que contiene definiciones de DTOs utilizados en el simulador
 * para evitar dependencias externas problemáticas.
 * 
 * @author Equipo1
 */
public class DTOUtils {
    
    /**
     * DTO para representar una lectura de un sensor
     */
    public static class LecturaDTO {
        
        private String idSensor;
        private String macAddress;
        private String marca;
        private String modelo;
        private String magnitud;
        private String unidad;
        private double valor;
        private Date fechaHora;

        public LecturaDTO(String idSensor, String macAddress, String marca, String modelo, 
                         String magnitud, String unidad, double valor, Date fechaHora) {
            this.idSensor = idSensor;
            this.macAddress = macAddress;
            this.marca = marca;
            this.modelo = modelo;
            this.magnitud = magnitud;
            this.unidad = unidad;
            this.valor = valor;
            this.fechaHora = fechaHora;
        }

        // Getters y setters (necesarios para la serialización correcta con Gson)
        public String getIdSensor() { return idSensor; }
        public void setIdSensor(String idSensor) { this.idSensor = idSensor; }
        
        public String getMacAddress() { return macAddress; }
        public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
        
        public String getMarca() { return marca; }
        public void setMarca(String marca) { this.marca = marca; }
        
        public String getModelo() { return modelo; }
        public void setModelo(String modelo) { this.modelo = modelo; }
        
        public String getMagnitud() { return magnitud; }
        public void setMagnitud(String magnitud) { this.magnitud = magnitud; }
        
        public String getUnidad() { return unidad; }
        public void setUnidad(String unidad) { this.unidad = unidad; }
        
        public double getValor() { return valor; }
        public void setValor(double valor) { this.valor = valor; }
        
        public Date getFechaHora() { return fechaHora; }
        public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }
    }
}