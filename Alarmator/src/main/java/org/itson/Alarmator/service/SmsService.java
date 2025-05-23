package org.itson.Alarmator.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    public void enviarSms(String destinatario, String mensaje) {
        Twilio.init(accountSid, authToken);
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(destinatario),
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                mensaje
        ).create();
        System.out.println("SMS enviado a: " + destinatario);
    }
}