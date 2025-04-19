package org.itson.Lecturas;

import org.itson.Lecturas.controller.LecturasController;
import org.itson.Lecturas.dtos.LecturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ProcesadorLecturas {
    @Autowired
    private LecturasController lecturasController;

    private static final int INTERVALO = 5000; // 5 segundos

    // Thread para guardar las lecturas periódicamente
    public void iniciar(ConcurrentHashMap<String, LecturaDTO> mapaLecturas) {
        new Thread(() -> {
            while (true) {
                try {
                    // Esperar el intervalo configurado
                    Thread.sleep(INTERVALO);

                    if (mapaLecturas.isEmpty()) {
                        System.out.println("No hubo lecturas nuevas en los últimos 5 segundos.");
                        continue;
                    }

                    // Procesar copia del mapa y limpiar el original
                    Map<String, LecturaDTO> copia = new ConcurrentHashMap<>(mapaLecturas);
                    mapaLecturas.clear();

                    // Iterar sobre todas las lecturas copiadas del mapa (una por cada sensor)
                    for (LecturaDTO lectura : copia.values()) {
                        // Se registra la lectura
                        ResponseEntity<?> respuesta = lecturasController.registrarLectura(lectura);
                        if (respuesta.getStatusCode().is2xxSuccessful()) { // Si todo salió bien
                            lectura = (LecturaDTO) respuesta.getBody(); // Obtener datos completos de la lectura
                        }
                        else if (respuesta.getStatusCode().is4xxClientError()) { // Si todo salió mal
                            System.out.println(respuesta.getBody());
                            continue;
                        }
                        imprimirLectura(lectura);
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
        String invernadero = lectura.getInvernadero();
        String sector = lectura.getSector();
        String fila = lectura.getFila();
        float valor = lectura.getValor();
        Date fechaHora = lectura.getFechaHora();
        String fechaHoraFormateada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fechaHora);

        System.out.printf("""
                ┌-----------------------------------------------------------┐
                |                     LECTURA GUARDADA                      |
                ├------------------┬----------------------------------------┤
                | ID sensor        | %-38s |
                | MAC address      | %-38s |
                | Marca            | %-38s |
                | Modelo           | %-38s |
                | Tipo de lectura  | %-38s |
                | Valor            | %-38.2f |
                | Magnitud         | %-38s |
                | Invernadero      | %-38s |
                | Sector           | %-38s |
                | Fila             | %-38s |
                | Hora             | %-38s |
                └------------------┴----------------------------------------┘
                """, idSensor, macAddress, marca, modelo, tipoLectura, valor, magnitud, invernadero, sector, fila, fechaHoraFormateada);
    }
}