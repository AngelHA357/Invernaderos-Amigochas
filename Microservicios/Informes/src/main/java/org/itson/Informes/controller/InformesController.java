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

    /**
     * Endpoint GET para obtener informes (lecturas) filtrados.
     * Consulta la base de datos LOCAL del microservicio de Informes.
     *
     * @param idInvernadero ID del invernadero.
     * @param fechaInicio   Fecha de inicio (ISO DateTime).
     * @param fechaFin      Fecha de fin (ISO DateTime).
     * @param magnitud      Tipo de magnitud.
     * @return ResponseEntity con la lista de InformeLectura (o DTOs) o un error.
     */
    @GetMapping("/filtradas")
    public ResponseEntity<?> obtenerInformeFiltrado(
            @RequestParam String idInvernadero,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaFin,
            @RequestParam String magnitud
    ) {
        try {
            List<InformeLectura> resultado = informesService.obtenerInformesFiltrados(
                    idInvernadero, fechaInicio, fechaFin, magnitud
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

}
