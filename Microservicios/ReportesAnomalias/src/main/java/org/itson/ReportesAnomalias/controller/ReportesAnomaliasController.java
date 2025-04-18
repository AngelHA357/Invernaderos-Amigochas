package org.itson.ReportesAnomalias.controller;

import org.itson.ReportesAnomalias.dtos.ReporteAnomaliaDTO;
import org.itson.ReportesAnomalias.service.ReportesAnomaliasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reportesAnomalias")
public class ReportesAnomaliasController {

    @Autowired
    private ReportesAnomaliasService reportesAnomaliasService;

    @GetMapping("/query")
    public ResponseEntity<?> obtenerReportesPorPeriodo(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin
    ) {
        return null;
    }

    @GetMapping("/invernadero/{id}")
    public ResponseEntity<?> obtenerReportesInvernadero(@PathVariable String id) {
        return null;
    }

    @GetMapping("/sensor/{id}")
    public ResponseEntity<?> obtenerReportesSensor(@PathVariable String id) {
        return null;
    }

    @GetMapping("/query")
    public ResponseEntity<?> obtenerReportesMagnitud(@RequestParam("magnitud") String magnitud) {
        return null;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarReporte(@RequestBody ReporteAnomaliaDTO reporte) {
        return null;
    }

}
