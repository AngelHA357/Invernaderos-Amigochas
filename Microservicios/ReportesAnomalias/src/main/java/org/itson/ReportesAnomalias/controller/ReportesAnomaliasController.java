package org.itson.ReportesAnomalias.controller;

import org.itson.ReportesAnomalias.dtos.AnomaliaDTO;
import org.itson.ReportesAnomalias.dtos.ReporteAnomaliaDTO;
import org.itson.ReportesAnomalias.excepciones.ReportesAnomaliasServiceException;
import org.itson.ReportesAnomalias.service.ReportesAnomaliasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reportesAnomalias")
public class ReportesAnomaliasController {

    @Autowired
    private ReportesAnomaliasService reportesAnomaliasService;

    @GetMapping("/query")
    public ResponseEntity<?> obtenerAnomaliasPorPeriodo(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Calendar inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Calendar fin
    ) {
        List<AnomaliaDTO> anomalias = reportesAnomaliasService.obtenerAnomaliasPorPeriodo(inicio, fin);
        ResponseEntity<List<AnomaliaDTO>> response = new ResponseEntity<>(anomalias, HttpStatus.OK);
        return response;
    }

    @GetMapping("/invernadero/{id}")
    public ResponseEntity<?> obtenerAnomaliasPorInvernadero(@PathVariable String idInvernadero) {
        List<AnomaliaDTO> anomalias = reportesAnomaliasService.obtenerAnomaliasPorInvernadero(idInvernadero);
        ResponseEntity<List<AnomaliaDTO>> response = new ResponseEntity<>(anomalias, HttpStatus.OK);
        return response;
    }

    @GetMapping("/sensor/{id}")
    public ResponseEntity<?> obtenerAnomaliasPorSensor(@PathVariable String idSensor) {
        List<AnomaliaDTO> anomalias = reportesAnomaliasService.obtenerAnomaliasPorSensor(idSensor);
        ResponseEntity<List<AnomaliaDTO>> response = new ResponseEntity<>(anomalias, HttpStatus.OK);
        return response;
    }

    @GetMapping("/query")
    public ResponseEntity<?> obtenerAnomaliasPorMagnitud(@RequestParam("magnitud") String magnitud) {
        List<AnomaliaDTO> anomalias = reportesAnomaliasService.obtenerAnomaliasPorMagnitud(magnitud);
        ResponseEntity<List<AnomaliaDTO>> response = new ResponseEntity<>(anomalias, HttpStatus.OK);
        return response;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarAnomalia(@RequestBody AnomaliaDTO anomalia) {
        try {
            AnomaliaDTO anomaliaRegistrada = reportesAnomaliasService.registrarAnomalia(anomalia);
            ResponseEntity<AnomaliaDTO> response = new ResponseEntity<>(anomaliaRegistrada, HttpStatus.OK);
            return response;
        } catch (ReportesAnomaliasServiceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    @GetMapping()
    public ResponseEntity<?> obtenerReporte(@RequestBody AnomaliaDTO anomalia) {
        ReporteAnomaliaDTO reporteObtenido = reportesAnomaliasService.obtenerReporte(anomalia);
        ResponseEntity<ReporteAnomaliaDTO> response = new ResponseEntity<>(reporteObtenido, HttpStatus.OK);
        return response;
    }

    public ResponseEntity<?> registrarReporte(@RequestBody ReporteAnomaliaDTO reporte) {
        try {
            ReporteAnomaliaDTO reporteRegistrado = reportesAnomaliasService.registrarReporte(reporte);
            ResponseEntity<ReporteAnomaliaDTO> response = new ResponseEntity<>(reporteRegistrado, HttpStatus.OK);
            return response;
        } catch (ReportesAnomaliasServiceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }
}
