package com.biodiversity.demo.controller;

import com.biodiversity.demo.model.Specie;
import com.biodiversity.demo.service.SpecieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/species")
public class SpecieController {

    @Autowired
    private SpecieService specieService;

    @GetMapping
    public ResponseEntity<List<Specie>> getAllSpecies(@RequestHeader("Authorization") String authToken) {
        return specieService.getAllSpecies(authToken);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Specie> getSpecieById(@RequestHeader("Authorization") String authToken,
            @PathVariable String id) {
        return specieService.getSpecieById(authToken, id);
    }

    @PostMapping
    public ResponseEntity<Specie> createSpecie(@RequestHeader("Authorization") String authToken,
            @RequestBody Specie specie) {
        return specieService.createSpecie(authToken, specie);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Specie> updateSpecie(@RequestHeader("Authorization") String authToken,
            @PathVariable String id,
            @RequestBody Specie specie) {
        return specieService.updateSpecie(authToken, id, specie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecie(@RequestHeader("Authorization") String authToken,
            @PathVariable String id) {
        return specieService.deleteSpecie(authToken, id);
    }
}