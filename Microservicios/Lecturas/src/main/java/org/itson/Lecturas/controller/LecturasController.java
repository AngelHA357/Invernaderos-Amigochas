package org.itson.Lecturas.controller;

import org.itson.Lecturas.dtos.LecturaDTO;
import org.itson.Lecturas.excepciones.LecturasException;
import org.itson.Lecturas.service.LecturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/lecturas")
public class LecturasController {
    // Para inyectar dependencias automáticamente
    @Autowired
    private LecturaService lecturaService;

    /**
     * Metodo que maneja las peticiones GET. Devuelve todas las lecturas que se encuentren en la base de datos.
     *
     * @return La respuesta HTTP con las lecturas encontrados y el status 200. Se devuelve un error si no se encontró nada.
     */
    @GetMapping({"", "/"})
    public ResponseEntity<?> obtenerTodasLecturas() {
        try {
            List<LecturaDTO> lecturas = lecturaService.obtenerTodasLecturas(); // Se obtienen las lecturas.
            ResponseEntity<List<LecturaDTO>> respuesta = new ResponseEntity<>(lecturas, HttpStatus.OK); // Se crea la respuesta.
            return respuesta;
        } catch (LecturasException gse) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", gse.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    /**
     * Método que maneja las peticiones HTTP POST.
     *
     * @param lectura Lectura a registrar.
     * @return La respuesta HTTP la lectura agregada y el código 200. Se devuelve un error si no se puede registrar.
     */
    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registrarLectura(@RequestBody LecturaDTO lectura) {
        try {
            lectura = lecturaService.registrarLectura(lectura); // Se registra la lectura y se obtiene con su ID.
            ResponseEntity<LecturaDTO> respuesta = new ResponseEntity<>(lectura, HttpStatus.OK); // Se crea la respuesta.
            return respuesta;
        } catch (LecturasException gse) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", gse.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    /**
     * Maneja peticiones GET para obtener lecturas filtradas por invernadero, rango de fechas y tipo de magnitud.
     *
     * @param idInvernadero ID del invernadero a filtrar.
     * @param fechaInicio Fecha de inicio del rango (formato ISO DateTime: YYYY-MM-DDTHH:mm:ss.sssZ).
     * @param fechaFin Fecha de fin del rango (formato ISO DateTime: YYYY-MM-DDTHH:mm:ss.sssZ).
     * @param magnitud Tipo de magnitud a filtrar (ej: "Temperatura").
     * @return Respuesta HTTP con la lista de LecturaDTO filtradas (status 200 OK),
     * o un error (status 404 NOT_FOUND) si ocurre un problema o no se encuentran lecturas.
     * Devuelve una lista vacía con status 200 OK si no hay lecturas que coincidan.
     */
    @GetMapping("/filtradas")
    public ResponseEntity<?> obtenerLecturasFiltradas(
            @RequestParam String idInvernadero,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaFin,
            @RequestParam String magnitud
    ) {
        try {
            List<LecturaDTO> lecturasFiltradas = lecturaService.obtenerLecturasFiltradas(
                    idInvernadero, fechaInicio, fechaFin, magnitud
            );

            return new ResponseEntity<>(lecturasFiltradas, HttpStatus.OK);

        } catch (LecturasException le) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", le.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al procesar la solicitud: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}
