package org.itson.Anomalyzer.controller;

import org.itson.Anomalyzer.dtos.AnomaliaResponseDTO;
import org.itson.Anomalyzer.service.AnomalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/anomalyzer") // Ruta base para este controlador
public class AnomalyzerController {

    @Autowired
    private AnomalyzerService anomalyzerService;

    @GetMapping("/anomalias")
    public ResponseEntity<?> obtenerAnomalias(
            @RequestParam String fechaInicio, // Espera YYYY-MM-DD
            @RequestParam String fechaFin     // Espera YYYY-MM-DD
    ) {
        try {
            List<AnomaliaResponseDTO> anomalias = anomalyzerService.obtenerAnomaliasPorFechas(fechaInicio, fechaFin);
            if (anomalias.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content si está vacío
            }
            return ResponseEntity.ok(anomalias);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en AnomalyzerController al obtener anomalías: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al procesar la solicitud de anomalías.");
        }
    }
}
