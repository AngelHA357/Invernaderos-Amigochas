package org.itson.ReportesAnomalias.controller;

import org.bson.types.ObjectId;
import org.itson.ReportesAnomalias.dtos.AnomaliaDTO;
import org.itson.ReportesAnomalias.dtos.ReporteAnomaliaDTO;
import org.itson.ReportesAnomalias.excepciones.ReportesAnomaliasServiceException;
import org.itson.ReportesAnomalias.service.ReportesAnomaliasService;
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
@RequestMapping("/api/v1/reportesAnomalias")
public class ReportesAnomaliasController {

    @Autowired
    private ReportesAnomaliasService reportesAnomaliasService;

    @GetMapping({"", "/"})
    public ResponseEntity<?> obtenerTodasLasAnomalias() {
        try {
            List<AnomaliaDTO> anomalias = reportesAnomaliasService.obtenerAnomalias();
            ResponseEntity<List<AnomaliaDTO>> response = new ResponseEntity<>(anomalias, HttpStatus.OK);
            return response;
        } catch (ReportesAnomaliasServiceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    @GetMapping("/periodo/query")
    public ResponseEntity<?> obtenerAnomaliasPorPeriodo(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fin
    ) {
        try {
            List<AnomaliaDTO> anomalias = reportesAnomaliasService.obtenerAnomaliasPorPeriodo(inicio, fin);
            ResponseEntity<List<AnomaliaDTO>> response = new ResponseEntity<>(anomalias, HttpStatus.OK);
            return response;
        } catch (ReportesAnomaliasServiceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    @GetMapping("/invernadero/{id}")
    public ResponseEntity<?> obtenerAnomaliasPorInvernadero(@PathVariable String id) {
        try {
            List<AnomaliaDTO> anomalias = reportesAnomaliasService.obtenerAnomaliasPorInvernadero(id);
            ResponseEntity<List<AnomaliaDTO>> response = new ResponseEntity<>(anomalias, HttpStatus.OK);
            return response;
        } catch (ReportesAnomaliasServiceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    @GetMapping("/sensor/{id}")
    public ResponseEntity<?> obtenerAnomaliasPorSensor(@PathVariable String id) {
        try {
            List<AnomaliaDTO> anomalias = reportesAnomaliasService.obtenerAnomaliasPorSensor(id);
            ResponseEntity<List<AnomaliaDTO>> response = new ResponseEntity<>(anomalias, HttpStatus.OK);
            return response;
        } catch (ReportesAnomaliasServiceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    @GetMapping("/magnitud/{magnitud}")
    public ResponseEntity<?> obtenerAnomaliasPorMagnitud(@PathVariable("magnitud") String magnitud) {
        try {
            List<AnomaliaDTO> anomalias = reportesAnomaliasService.obtenerAnomaliasPorMagnitud(magnitud);
            ResponseEntity<List<AnomaliaDTO>> response = new ResponseEntity<>(anomalias, HttpStatus.OK);
            return response;
        } catch (ReportesAnomaliasServiceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    @GetMapping("")
    public ResponseEntity<?> obtenerReporte(@RequestBody AnomaliaDTO anomalia) {
        try {
            ReporteAnomaliaDTO reporteObtenido = reportesAnomaliasService.obtenerReporte(anomalia);
            ResponseEntity<ReporteAnomaliaDTO> response = new ResponseEntity<>(reporteObtenido, HttpStatus.OK);
            return response;
        } catch (ReportesAnomaliasServiceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    @PostMapping("/registrarReporte")
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

    @GetMapping("/existe")
    public ResponseEntity<?> existeReporteParaAnomalia(@RequestParam("anomaliaId") String anomaliaId) {
        boolean existe = reportesAnomaliasService.existeReporteParaAnomalia(anomaliaId);
        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("existe", existe);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}") 
    public ResponseEntity<?> obtenerAnomaliaPorSuId(@PathVariable("id") String id) {
        try {
            if (!ObjectId.isValid(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("mensaje", "El ID de anomalía proporcionado no es válido.");
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
            AnomaliaDTO anomalia = reportesAnomaliasService.obtenerAnomaliaPorId(id);
            return ResponseEntity.ok(anomalia);
        } catch (ReportesAnomaliasServiceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            System.err.println("Error en ReportesAnomaliasController al obtener anomalía por ID: " + e.getMessage());
            e.printStackTrace();
            error.put("mensaje", "Error interno al obtener la anomalía.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Verifica si existe un reporte para una anomalía específica.
     * @param anomaliaId ID de la anomalía
     * @return true si existe reporte, false en caso contrario
     */
    @GetMapping("/verificar/{anomaliaId}")
    public ResponseEntity<Boolean> verificarReporteExistente(@PathVariable("anomaliaId") String anomaliaId) {
        try {
            if (!ObjectId.isValid(anomaliaId)) {
                return ResponseEntity.badRequest().body(false);
            }
            boolean existe = reportesAnomaliasService.existeReporteParaAnomalia(anomaliaId);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            System.err.println("Error al verificar reporte existente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    /**
     * Obtiene un reporte asociado a una anomalía específica.
     * @param anomaliaId ID de la anomalía
     * @return El reporte encontrado o error si no existe
     */
    @GetMapping("/reporte-de-anomalia/{anomaliaId}")
    public ResponseEntity<?> obtenerReportePorAnomaliaId(@PathVariable("anomaliaId") String anomaliaId) {
        try {
            if (!ObjectId.isValid(anomaliaId)) {
                Map<String, String> error = new HashMap<>();
                error.put("mensaje", "El ID de anomalía proporcionado no es válido.");
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
            ReporteAnomaliaDTO reporte = reportesAnomaliasService.obtenerReportePorAnomaliaId(anomaliaId);
            return ResponseEntity.ok(reporte);
        } catch (ReportesAnomaliasServiceException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al obtener reporte: " + e.getMessage());
            System.err.println("Error al obtener reporte por ID de anomalía: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}
