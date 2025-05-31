package com.biodiversity.demo.service;

import com.biodiversity.demo.model.Observation;
import com.biodiversity.demo.dto.CreateObservationDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ObservationService extends SupabaseService {

    private static final String OBSERVATIONS_ENDPOINT = "/rest/v1/observations";

    public ResponseEntity<List<Observation>> getAllObservations(String authToken) {
        HttpHeaders headers = createHeaders(authToken);
        return restTemplate.exchange(
                supabaseConfig.getSupabaseUrl() + OBSERVATIONS_ENDPOINT,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Observation>>() {
                });
    }

    public ResponseEntity<List<Observation>> getObservationById(String authToken, String id) {
        HttpHeaders headers = createHeaders(authToken);
        return restTemplate.exchange(
                supabaseConfig.getSupabaseUrl() + OBSERVATIONS_ENDPOINT + "?id_observation=eq." + id,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Observation>>() {
                });
    }

    public ResponseEntity<List<Observation>> getObservationsByUserId(String authToken, String userId) {
        HttpHeaders headers = createHeaders(authToken);
        return restTemplate.exchange(
                supabaseConfig.getSupabaseUrl() + OBSERVATIONS_ENDPOINT + "?id_observer_user=eq." + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Observation>>() {
                });
    }

    public ResponseEntity<List<Observation>> createObservation(String authToken, CreateObservationDTO observationDTO) {
        HttpHeaders headers = createHeaders(authToken);
        headers.set("Prefer", "return=representation");
        return restTemplate.exchange(
                supabaseConfig.getSupabaseUrl() + OBSERVATIONS_ENDPOINT,
                HttpMethod.POST,
                new HttpEntity<>(observationDTO, headers),
                new ParameterizedTypeReference<List<Observation>>() {
                });
    }

    public ResponseEntity<Observation> updateObservation(String authToken, String id, Observation observation) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(OBSERVATIONS_ENDPOINT + "?id=eq." + id, HttpMethod.PATCH, headers, observation,
                Observation.class);
    }

    public ResponseEntity<Void> deleteObservation(String authToken, String id) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(OBSERVATIONS_ENDPOINT + "?id=eq." + id, HttpMethod.DELETE, headers, null, Void.class);
    }
}