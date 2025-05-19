package org.itson.Alarmator.service;

import org.itson.Alarmator.dtos.AlarmaAnomaliaDTO;
import org.itson.Alarmator.dtos.AlarmaDTO;
import org.itson.Alarmator.dtos.AnomaliaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmatorService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private SmsService smsService;
    private final String CORREO_ADMINISTRADOR = "invernaderosamigochas@gmail.com";
    private final String NUMERO_ADMINISTRADOR = "+5216441633550";

    public void dispararAlarma(AlarmaAnomaliaDTO alarmaAnomalia) {
        AlarmaDTO alarma = alarmaAnomalia.getAlarma();
        AnomaliaDTO anomalia = alarmaAnomalia.getAnomalia();

        if (alarma.getMedioNotificacion().contains("Email")) {
            String destinatario = CORREO_ADMINISTRADOR;
            String asunto = "Alarma activada: " + alarma.getIdAlarma();
            String mensaje = anomalia.getCausa();
            emailService.enviarCorreo(destinatario, asunto, mensaje);
        }

        if (alarma.getMedioNotificacion().contains("SMS")) {
            String destinatario = NUMERO_ADMINISTRADOR;
            String mensaje = "Alarma activada: " + alarma.getIdAlarma() + ". Causa: " + anomalia.getCausa();
            smsService.enviarSms(destinatario, mensaje);
        }
        System.out.println("Alarma disparada: " + alarma.getIdAlarma());
        System.out.println("Anomalia: " + anomalia.getIdSensor() + " - " + anomalia.getValor() + " " + anomalia.getUnidad());
    }
}
