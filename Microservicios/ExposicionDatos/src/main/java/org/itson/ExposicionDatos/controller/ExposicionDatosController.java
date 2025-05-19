package org.itson.ExposicionDatos.controller;

import org.itson.ExposicionDatos.dtos.LecturaDTO;
import org.itson.ExposicionDatos.exceptions.ExposicionDatosException;
import org.itson.ExposicionDatos.service.ExposicionDatosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/datos")
public class ExposicionDatosController {
    // Para inyectar dependencias automáticamente
    @Autowired
    private ExposicionDatosService exposicionDatosService;

    /**
     * Metodo que maneja las peticiones GET. Devuelve todas las lecturas que se encuentren en la base de datos.
     *
     * @return La respuesta HTTP con las lecturas encontrados y el status 200. Se devuelve un error si no se encontró nada.
     */
    @GetMapping({"", "/"})
    public ResponseEntity<?> obtenerTodasLecturas() {
        try {
            List<LecturaDTO> lecturas = exposicionDatosService.obtenerTodasLecturas(); // Se obtienen las lecturas.
            ResponseEntity<List<LecturaDTO>> respuesta = new ResponseEntity<>(lecturas, HttpStatus.OK); // Se crea la respuesta.
            return respuesta;
        } catch (ExposicionDatosException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }
}
