package com.biodiversity.demo.controller;

import com.biodiversity.demo.model.Observation;
import com.biodiversity.demo.service.ObservationService;
import com.biodiversity.demo.dto.CreateObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/observations")
public class ObservationController {

    private static final Logger logger = LoggerFactory.getLogger(ObservationController.class);

    @Autowired
    private ObservationService observationService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllObservations(
            @RequestHeader("Authorization") String authToken,
            @RequestParam(defaultValue = "1") int page) {
        logger.info("Solicitud recibida para obtener todas las observaciones, página: {}", page);
        ResponseEntity<List<Observation>> response = observationService.getAllObservations(authToken, page);
        List<Observation> observations = response.getBody();
        logger.info("Se encontraron {} observaciones en la página {}", observations != null ? observations.size() : 0,
                page);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);
        responseBody.put("data", observations);
        responseBody.put("page", page);
        responseBody.put("pageSize", 5);
        responseBody.put("count", observations != null ? observations.size() : 0);

        return ResponseEntity.status(response.getStatusCode()).body(responseBody);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getObservationById(@RequestHeader("Authorization") String authToken,
            @PathVariable String id) {
        logger.info("Solicitud recibida para obtener observación con ID: {}", id);
        ResponseEntity<List<Observation>> response = observationService.getObservationById(authToken, id);
        Observation obs = response.getBody() != null && !response.getBody().isEmpty() ? response.getBody().get(0)
                : null;

        Map<String, Object> responseBody = new HashMap<>();
        if (obs != null) {
            logger.info("Observación encontrada con ID: {}", id);
            responseBody.put("success", true);
            responseBody.put("data", obs);
        } else {
            logger.info("No se encontró observación con ID: {}", id);
            responseBody.put("success", false);
            responseBody.put("message", "Observación no encontrada");
        }

        return ResponseEntity.status(response.getStatusCode()).body(responseBody);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getObservationsByUserId(
            @RequestHeader("Authorization") String authToken,
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page) {
        logger.info("Solicitud recibida para obtener observaciones del usuario con ID: {}, página: {}", userId, page);
        ResponseEntity<List<Observation>> response = observationService.getObservationsByUserId(authToken, userId,
                page);
        List<Observation> observations = response.getBody();

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);
        responseBody.put("data", observations);
        responseBody.put("page", page);
        responseBody.put("pageSize", 5);
        responseBody.put("count", observations != null ? observations.size() : 0);

        logger.info("Se encontraron {} observaciones para el usuario {} en la página {}",
                observations != null ? observations.size() : 0, userId, page);
        return ResponseEntity.status(response.getStatusCode()).body(responseBody);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createObservation(
            @RequestHeader("Authorization") String authToken,
            @RequestBody CreateObservationDTO observationDTO) {
        ResponseEntity<List<Observation>> response = observationService.createObservation(authToken, observationDTO);
        Observation obs = response.getBody() != null && !response.getBody().isEmpty() ? response.getBody().get(0)
                : null;

        Map<String, Object> responseBody = new HashMap<>();
        if (obs != null) {
            responseBody.put("success", true);
            responseBody.put("data", obs);
            responseBody.put("message", "Observación creada exitosamente");
        } else {
            responseBody.put("success", false);
            responseBody.put("message", "Error al crear la observación");
        }

        logger.info("Respuesta enviada al cliente: {}", obs);
        return ResponseEntity.status(response.getStatusCode()).body(responseBody);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateObservation(@RequestHeader("Authorization") String authToken,
            @PathVariable String id,
            @RequestBody Observation observation) {
        ResponseEntity<Observation> response = observationService.updateObservation(authToken, id, observation);

        Map<String, Object> responseBody = new HashMap<>();
        if (response.getBody() != null) {
            responseBody.put("success", true);
            responseBody.put("data", response.getBody());
            responseBody.put("message", "Observación actualizada exitosamente");
        } else {
            responseBody.put("success", false);
            responseBody.put("message", "Error al actualizar la observación");
        }

        return ResponseEntity.status(response.getStatusCode()).body(responseBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteObservation(@RequestHeader("Authorization") String authToken,
            @PathVariable String id) {
        ResponseEntity<Void> response = observationService.deleteObservation(authToken, id);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", response.getStatusCode().is2xxSuccessful());
        responseBody.put("message", response.getStatusCode().is2xxSuccessful() ? "Observación eliminada exitosamente"
                : "Error al eliminar la observación");

        return ResponseEntity.status(response.getStatusCode()).body(responseBody);
    }
}