package org.itson.GestionSensoresMicroservicio.controller;

import org.itson.GestionSensoresMicroservicio.service.GestionSensoresService;
import org.itson.GestionSensoresMicroservicio.dtos.SensorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Clase controller del microservicio. Se encarga de manejar las peticiones HTTP.
 */
@RestController
@RequestMapping("/api/v1/gestionSensores")
@CrossOrigin
public class GestionSensoresController {
    // Para inyectar dependencias automáticamente
    @Autowired
    private GestionSensoresService gestionSensoresService;

    /**
     * Metodo que maneja las peticiones GET. Devuelve todos los sensores que se encuentren en la base de datos.
     *
     * @return Una entidad con los sensores encontrados y el status 200.
     */
    @GetMapping
    public ResponseEntity<List<SensorDTO>> obtenerTodosSensores() {
        return new ResponseEntity<>(gestionSensoresService.obtenerTodosSensores(), HttpStatus.OK);
    }
}
