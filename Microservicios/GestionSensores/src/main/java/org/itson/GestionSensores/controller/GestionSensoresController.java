package org.itson.GestionSensores.controller;

import org.itson.GestionSensores.excepciones.GestionSensoresException;
import org.itson.GestionSensores.dtos.SensorDTO;
import org.itson.GestionSensores.service.GestionSensoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase controller del microservicio. Se encarga de manejar las peticiones HTTP.
 */
@RestController
@RequestMapping("/api/v1/gestionSensores")
public class GestionSensoresController {
    // Para inyectar dependencias automáticamente
    @Autowired
    private GestionSensoresService gestionSensoresService;

    /**
     * Metodo que maneja las peticiones GET. Devuelve todos los sensores que se encuentren en la base de datos.
     *
     * @return La respuesta HTTP con los sensores encontrados y el status 200. Se devuelve un error si no se encontró nada.
     */
    @GetMapping({"", "/"})
    public ResponseEntity<?> obtenerTodosSensores() {
        try {
            List<SensorDTO> sensores = gestionSensoresService.obtenerTodosSensores(); // Se obtienen los sensores.
            ResponseEntity<List<SensorDTO>> respuesta = new ResponseEntity<>(sensores, HttpStatus.OK); // Se crea la respuesta.
            return respuesta;
        } catch (GestionSensoresException gse) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", gse.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    /**
     * Metodo que maneja las peticiones GET con un ID de un sensor. Devuelve el sensor de la base de datos que tenga el ID proporcionado.
     *
     * @param id ID del sensor a buscar.
     * @return La respuesta HTTP con el sensor encontrado y el código 200. Se devuelve un error si no se encontró el sensor.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerSensorPorId(@PathVariable String id) {
        try {
            SensorDTO sensor = gestionSensoresService.obtenerSensorPorId(id); // Se obtiene el sensor por ID.
            ResponseEntity<SensorDTO> respuesta = new ResponseEntity<>(sensor, HttpStatus.OK); // Se crea la respuesta.
            return respuesta;
        } catch (GestionSensoresException gse) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", gse.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    /**
     * Método que maneja las peticiones HTTP POST.
     *
     * @param sensor Sensor a registrar.
     * @return La respuesta HTTP con el sensor agregado y el código 200. Se devuelve un error si no se encontró el sensor.
     */
    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registrarSensor(@RequestBody SensorDTO sensor) {
        try {
            sensor = gestionSensoresService.registrarSensor(sensor); // Se registra el sensor y se obtiene con su ID.
            ResponseEntity<SensorDTO> respuesta = new ResponseEntity<>(sensor, HttpStatus.OK); // Se crea la respuesta.
            return respuesta;
        } catch (GestionSensoresException gse) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", gse.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    /**
     * Método que maneja las peticiones HTTP PUT.
     *
     * @param sensor Sensor a editar.
     * @return La respuesta HTTP con el sensor editado y el código 200. Se devuelve un error si no se encontró el sensor.
     */
    @PutMapping("/editar")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> editarSensor(@RequestBody SensorDTO sensor) {
        try {
            sensor = gestionSensoresService.editarSensor(sensor);
            ResponseEntity<SensorDTO> respuesta = new ResponseEntity<>(sensor, HttpStatus.OK); // Se crea la respuesta.
            return respuesta;
        } catch (GestionSensoresException gse) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", gse.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }

    /**
     * Método que maneja las peticiones HTTP DELETE.
     *
     * @param id ID del sensor a eliminar.
     * @return La respuesta HTTP con el código 200. Se devuelve un error si no se encontró el sensor.
     */
    @DeleteMapping("/eliminar/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> eliminarSensor(@PathVariable String id) {
        try {
            gestionSensoresService.eliminarSensor(id);
            ResponseEntity<Void> respuesta = new ResponseEntity<>(HttpStatus.OK); // Se crea la respuesta. Void indica que el body está vacío.
            return respuesta;
        } catch (GestionSensoresException gse) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", gse.getMessage()); // Se mapea el error del mensaje.
            ResponseEntity<Map<String, String>> respuesta = new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // Creamos la respuesta.
            return respuesta;
        }
    }
}
