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

@RestController
@RequestMapping("/api/observations")
public class ObservationController {

    private static final Logger logger = LoggerFactory.getLogger(ObservationController.class);

    @Autowired
    private ObservationService observationService;

    @GetMapping
    public ResponseEntity<List<Observation>> getAllObservations(@RequestHeader("Authorization") String authToken) {
        return observationService.getAllObservations(authToken);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Observation> getObservationById(@RequestHeader("Authorization") String authToken,
            @PathVariable String id) {
        ResponseEntity<List<Observation>> response = observationService.getObservationById(authToken, id);
        Observation obs = response.getBody() != null && !response.getBody().isEmpty() ? response.getBody().get(0)
                : null;
        return ResponseEntity.status(response.getStatusCode()).body(obs);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Observation>> getObservationsByUserId(@RequestHeader("Authorization") String authToken,
            @PathVariable String userId) {
        return observationService.getObservationsByUserId(authToken, userId);
    }

    @PostMapping
    public ResponseEntity<Observation> createObservation(
            @RequestHeader("Authorization") String authToken,
            @RequestBody CreateObservationDTO observationDTO) {
        ResponseEntity<List<Observation>> response = observationService.createObservation(authToken, observationDTO);
        Observation obs = response.getBody() != null && !response.getBody().isEmpty() ? response.getBody().get(0)
                : null;
        logger.info("Respuesta enviada al cliente: {}", obs);
        return ResponseEntity.status(response.getStatusCode()).body(obs);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Observation> updateObservation(@RequestHeader("Authorization") String authToken,
            @PathVariable String id,
            @RequestBody Observation observation) {
        return observationService.updateObservation(authToken, id, observation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObservation(@RequestHeader("Authorization") String authToken,
            @PathVariable String id) {
        return observationService.deleteObservation(authToken, id);
    }
}