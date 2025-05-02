package org.itson.Informes.controller;

import org.itson.Informes.collections.InformeLectura;
import org.itson.Informes.service.InformeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/informes")
public class InformesController {

    @Autowired
    private InformeService informesService;

    @GetMapping("/filtradas")
    public ResponseEntity<?> obtenerInformeFiltrado(
            @RequestParam List<String> idsInvernadero,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaFin,
            @RequestParam List<String> magnitudes
    ) {
        try {
            List<InformeLectura> resultado = informesService.obtenerInformesFiltrados(
                    idsInvernadero, fechaInicio, fechaFin, magnitudes
            );
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } catch (IllegalArgumentException iae) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error en los parámetros: " + iae.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno al generar el informe: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint GET para obtener la lista de magnitudes únicas disponibles
     * en los datos de informes guardados localmente.
     * @return ResponseEntity con la lista de strings de magnitudes o un error 500.
     */
    @GetMapping("/magnitudesDisponibles")
    public ResponseEntity<?> obtenerMagnitudesDisponibles() {
        try {
            List<String> magnitudes = informesService.obtenerMagnitudesDisponibles();
            return ResponseEntity.ok(magnitudes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno al obtener lista de magnitudes: " + e.getMessage());
            System.err.println("Error en obtenerMagnitudesDisponibles: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

}
