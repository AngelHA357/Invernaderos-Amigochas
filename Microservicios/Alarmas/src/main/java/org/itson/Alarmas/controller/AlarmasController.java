package org.itson.Alarmas.controller;

import org.itson.Alarmas.dtos.AlarmaDTO;
import org.itson.Alarmas.exceptions.AlarmasException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.itson.Alarmas.service.AlarmasService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/alarmas")
public class AlarmasController {

    @Autowired
    private AlarmasService alarmasService;

    /**
     * Endpoint para obtener todas las alarmas.
     *
     * @return Lista de todas las alarmas o un mensaje de error si no se encuentran.
     */
    @GetMapping({"", "/"})
    public ResponseEntity<?> obtenerTodasLasAlarmas() {
        try {
            List<AlarmaDTO> alarmas = alarmasService.obtenerTodasLasAlarmas();
            return new ResponseEntity<>(alarmas, HttpStatus.OK);
        } catch (AlarmasException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para obtener una alarma por su ID.
     *
     * @param idAlarma El ID de la alarma a obtener.
     * @return La alarma encontrada o un mensaje de error si no se encuentra.
     */
    @GetMapping("/{idAlarma}")
    public ResponseEntity<?> obtenerAlarmaPorId(@PathVariable String idAlarma) {
        try {
            AlarmaDTO alarma = alarmasService.obtenerAlarmaPorId(idAlarma);
            return new ResponseEntity<>(alarma, HttpStatus.OK);
        } catch (AlarmasException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }


    /**
     * Endpoint para registrar una nueva alarma.
     *
     * @param alarma Los datos de la alarma a registrar.
     * @return La alarma registrada o un mensaje de error si el registro falla.
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarAlarma(@RequestBody AlarmaDTO alarma) {
        try {
            AlarmaDTO alarmaRegistrada = alarmasService.registrarAlarma(alarma);
            return new ResponseEntity<>(alarmaRegistrada, HttpStatus.OK);
        } catch (AlarmasException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para actualizar una alarma existente.
     *
     * @param alarma Los datos de la alarma a actualizar.
     * @return La alarma actualizada o un mensaje de error si la actualización falla.
     */
    @PutMapping("/editar")
    public ResponseEntity<?> editarAlarma(@RequestBody AlarmaDTO alarma) {
        try {
            AlarmaDTO alarmaActualizada = alarmasService.editarAlarma(alarma);
            return new ResponseEntity<>(alarmaActualizada, HttpStatus.OK);
        } catch (AlarmasException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para eliminar una alarma por su ID.
     *
     * @param idAlarma El ID de la alarma a eliminar.
     * @return Un mensaje de éxito o un mensaje de error si la eliminación falla.
     */
    @DeleteMapping("/eliminar/{idAlarma}")
    public ResponseEntity<?> eliminarAlarma(@PathVariable String idAlarma) {
        try {
            alarmasService.eliminarAlarma(idAlarma);
            Map<String, String> success = new HashMap<>();
            success.put("mensaje", "Alarma eliminada correctamente.");
            return new ResponseEntity<>(success, HttpStatus.OK);
        } catch (AlarmasException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
}