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
        return executeRequest(SPECIES_ENDPOINT + "?id=eq." + id, HttpMethod.GET, headers, null, Specie.class);
    }

    public ResponseEntity<Specie> createSpecie(String authToken, Specie specie) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(SPECIES_ENDPOINT, HttpMethod.POST, headers, specie, Specie.class);
    }

    public ResponseEntity<Specie> updateSpecie(String authToken, String id, Specie specie) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(SPECIES_ENDPOINT + "?id=eq." + id, HttpMethod.PATCH, headers, specie, Specie.class);
    }

    public ResponseEntity<Void> deleteSpecie(String authToken, String id) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(SPECIES_ENDPOINT + "?id=eq." + id, HttpMethod.DELETE, headers, null, Void.class);
    }
}