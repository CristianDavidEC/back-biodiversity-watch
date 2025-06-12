package com.biodiversity.demo.service;

import com.biodiversity.demo.model.Specie;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecieService extends SupabaseService {

    private static final String SPECIES_ENDPOINT = "/rest/v1/species";

    public ResponseEntity<List<Specie>> getAllSpecies(String authToken) {
        HttpHeaders headers = createHeaders(authToken);
        return restTemplate.exchange(
                supabaseConfig.getSupabaseUrl() + SPECIES_ENDPOINT,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Specie>>() {
                });
    }

    public ResponseEntity<Specie> getSpecieById(String authToken, String id) {
        HttpHeaders headers = createHeaders(authToken);
        ResponseEntity<List<Specie>> response = restTemplate.exchange(
                supabaseConfig.getSupabaseUrl() + SPECIES_ENDPOINT + "?id_specie=eq." + id,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Specie>>() {
                });

        if (response.getBody() != null && !response.getBody().isEmpty()) {
            return ResponseEntity.ok(response.getBody().get(0));
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Specie> createSpecie(String authToken, Specie specie) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(SPECIES_ENDPOINT, HttpMethod.POST, headers, specie, Specie.class);
    }

    public ResponseEntity<Specie> updateSpecie(String authToken, String id, Specie specie) {
        HttpHeaders headers = createHeaders(authToken);
        ResponseEntity<List<Specie>> response = restTemplate.exchange(
                supabaseConfig.getSupabaseUrl() + SPECIES_ENDPOINT + "?id_specie=eq." + id,
                HttpMethod.PATCH,
                new HttpEntity<>(specie, headers),
                new ParameterizedTypeReference<List<Specie>>() {
                });

        if (response.getBody() != null && !response.getBody().isEmpty()) {
            return ResponseEntity.ok(response.getBody().get(0));
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Void> deleteSpecie(String authToken, String id) {
        HttpHeaders headers = createHeaders(authToken);
        ResponseEntity<List<Specie>> response = restTemplate.exchange(
                supabaseConfig.getSupabaseUrl() + SPECIES_ENDPOINT + "?id_specie=eq." + id,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Specie>>() {
                });

        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<List<Specie>> getSpecieByScientificName(String authToken, String scientificName) {
        HttpHeaders headers = createHeaders(authToken);
        return restTemplate.exchange(
                supabaseConfig.getSupabaseUrl() + SPECIES_ENDPOINT + "?scientific_name=eq." + scientificName,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Specie>>() {
                });
    }
}