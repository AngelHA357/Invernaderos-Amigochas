package org.itson.Informes.controller;

// import org.itson.Informes.collections.InformeLectura; // Ya no se devuelve directamente si no se guarda
import org.itson.Informes.dtos.InformeResponseDTO; // El nuevo DTO de respuesta
import org.itson.Informes.dtos.InvernaderoBasicoDTO;
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
    private InformeService informeService; // Nombre corregido de la variable

    /**
     * Endpoint GET para generar un informe de lecturas filtradas y enriquecidas.
     * Las lecturas se obtienen de la colección principal, se enriquecen vía gRPC
     * con GestionSensores, y se devuelven para ser mostradas en el frontend.
     * El informe generado no se persiste en la base de datos de Informes.
     *
     * @param idsInvernadero Lista de IDs de invernadero para filtrar.
     * @param fechaInicio Fecha de inicio del periodo del informe.
     * @param fechaFin Fecha de fin del periodo del informe.
     * @param magnitudes Lista de magnitudes a incluir en el informe.
     * @return ResponseEntity con InformeResponseDTO o un mensaje de error.
     */
    @GetMapping("/filtradas") // O podrías renombrarlo a "/generar", "/vista-previa", etc.
    public ResponseEntity<?> generarInformeParaFrontend( // Nombre del método cambiado para claridad
                                                         @RequestParam List<String> idsInvernadero,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaInicio,
                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaFin,
                                                         @RequestParam List<String> magnitudes
    ) {
        try {
            // Llamar al nuevo método del servicio que devuelve InformeResponseDTO
            InformeResponseDTO informeResponse = informeService.generarInformeParaFrontend(
                    idsInvernadero, fechaInicio, fechaFin, magnitudes
            );

            if (informeResponse.getLecturasEnriquecidas() == null || informeResponse.getLecturasEnriquecidas().isEmpty()) {
                // Si no hay lecturas (incluso después de intentar obtenerlas),
                // podrías devolver NOT_FOUND o seguir con OK y el mensaje "Informe Vacío".
                // El servicio ya devuelve un DTO con título "Informe Vacío - Sin Lecturas" en este caso.
                System.out.println("InformesController: No se generaron lecturas para el informe solicitado.");
            }

            return new ResponseEntity<>(informeResponse, HttpStatus.OK);

        } catch (IllegalArgumentException iae) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error en los parámetros: " + iae.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException re) { // Captura más específica si generas excepciones personalizadas en el servicio
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al generar el informe: " + re.getMessage());
            // Loguear el error en el servidor para más detalles
            re.printStackTrace(); // Para depuración, considera un logger en producción
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) { // Captura genérica para otros errores inesperados
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno inesperado al procesar la solicitud: " + e.getMessage());
            e.printStackTrace(); // Para depuración
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/invernaderos-desde-lecturas")
    public ResponseEntity<?> obtenerListaInvernaderosDesdeLecturas() {
        try {
            List<InvernaderoBasicoDTO> invernaderos = informeService.obtenerInvernaderosDesdeLecturas();
            if (invernaderos.isEmpty()) {
                return ResponseEntity.noContent().build(); // O OK con lista vacía
            }
            return ResponseEntity.ok(invernaderos);
        } catch (Exception e) {
            // Loggear el error
            System.err.println("Error al obtener invernaderos desde lecturas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener lista de invernaderos desde lecturas.");
        }
    }

    /**
     * Endpoint GET para obtener la lista de magnitudes únicas disponibles
     * desde la colección principal de lecturas.
     * @return ResponseEntity con la lista de strings de magnitudes o un error 500.
     */
    @GetMapping("/magnitudesDisponibles")
    public ResponseEntity<?> obtenerMagnitudesDisponibles() {
        try {
            List<String> magnitudes = informeService.obtenerMagnitudesDisponibles();
            return ResponseEntity.ok(magnitudes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error interno al obtener lista de magnitudes: " + e.getMessage());
            System.err.println("Error en InformesController.obtenerMagnitudesDisponibles: " + e.getMessage());
            e.printStackTrace(); // Para depuración
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

