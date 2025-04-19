package org.itson.Lecturas;

import org.itson.Lecturas.controller.LecturasController;
import org.itson.Lecturas.dtos.LecturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ProcesadorLecturas {
    @Autowired
    private LecturasController lecturasController;

    private static final int INTERVALO = 5000; // 5 segundos

    // Thread para guardar las lecturas periódicamente
    public void iniciar(AtomicReference<LecturaDTO> refLectura) {
        new Thread(() -> {
            while (true) {
                try {
                    // Esperar el intervalo configurado
                    Thread.sleep(INTERVALO);

                    // Obtener la última lectura de manera atómica
                    LecturaDTO lectura = refLectura.getAndSet(null);

                    if (lectura != null) {
                        // Guardar la lectura
                        lecturasController.registrarLectura(lectura);

                        // Imprimir información de la lectura guardada
                        imprimirLectura(lectura);
                    } else {
                        System.out.println("No hubo nueva lectura en este periodo de 5 segundos.\n");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    // Método para imprimir la información de la lectura
    private void imprimirLectura(LecturaDTO lectura) {
        String idSensor = lectura.getIdSensor();
        String macAddress = lectura.getMacAddress();
        String marca = lectura.getMarca();
        String modelo = lectura.getModelo();
        String tipoLectura = lectura.getTipoLectura();
        String magnitud = lectura.getMagnitud();
        float valor = lectura.getValor();
        Date fechaHora = lectura.getFechaHora();
        String fechaHoraFormateada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fechaHora);

        System.out.printf("""
                +-----------------------------------------------------------+
                |                     LECTURA GUARDADA                      |
                +------------------+----------------------------------------+
                | ID sensor        | %-38s |
                | MAC address      | %-38s |
                | Marca            | %-38s |
                | Modelo           | %-38s |
                | Tipo de lectura  | %-38s |
                | Valor            | %-38.2f |
                | Magnitud         | %-38s |
                | Hora             | %-38s |
                +------------------+----------------------------------------+
                """, idSensor, macAddress, marca, modelo, tipoLectura, valor, magnitud, fechaHoraFormateada);
    }
}