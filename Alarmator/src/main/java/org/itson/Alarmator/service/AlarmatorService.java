package org.itson.Alarmator.service;

import org.itson.Alarmator.dtos.AlarmaAnomaliaDTO;
import org.itson.Alarmator.dtos.AlarmaDTO;
import org.itson.Alarmator.dtos.AnomaliaDTO;
import org.springframework.stereotype.Service;

@Service
public class AlarmatorService {
    public void dispararAlarma(AlarmaAnomaliaDTO alarmaAnomalia) {
        AlarmaDTO alarma = alarmaAnomalia.getAlarma();
        AnomaliaDTO anomalia = alarmaAnomalia.getAnomalia();

        if (alarma.getMedioNotificacion().equalsIgnoreCase("Email")) {
            // Enviar correo electrónico
        } else if (alarma.getMedioNotificacion().equalsIgnoreCase("SMS")) {
            // Enviar SMS
        }
        System.out.println("Alarma disparada: " + alarma.getIdAlarma());
        System.out.println("Anomalia: " + anomalia.getIdSensor() + " - " + anomalia.getValor() + " " + anomalia.getUnidad());
    }
}
