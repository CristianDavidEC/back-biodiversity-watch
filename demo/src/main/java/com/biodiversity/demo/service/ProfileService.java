package com.biodiversity.demo.service;

import com.biodiversity.demo.model.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService extends SupabaseService {

    private static final String PROFILES_ENDPOINT = "/rest/v1/profiles";

    public ResponseEntity<List<Profile>> getAllProfiles(String authToken) {
        HttpHeaders headers = createHeaders(authToken);
        return restTemplate.exchange(
                supabaseConfig.getSupabaseUrl() + PROFILES_ENDPOINT,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Profile>>() {
                });
    }

    public ResponseEntity<Profile> getProfileById(String authToken, String id) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(PROFILES_ENDPOINT + "?id=eq." + id, HttpMethod.GET, headers, null, Profile.class);
    }

    public ResponseEntity<Profile> getProfileByEmail(String authToken, String email) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(PROFILES_ENDPOINT + "?email=eq." + email, HttpMethod.GET, headers, null, Profile.class);
    }

    public ResponseEntity<Profile> createProfile(String authToken, Profile profile) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(PROFILES_ENDPOINT, HttpMethod.POST, headers, profile, Profile.class);
    }

    public ResponseEntity<Profile> updateProfile(String authToken, String id, Profile profile) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(PROFILES_ENDPOINT + "?id=eq." + id, HttpMethod.PATCH, headers, profile, Profile.class);
    }

    public ResponseEntity<Void> deleteProfile(String authToken, String id) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(PROFILES_ENDPOINT + "?id=eq." + id, HttpMethod.DELETE, headers, null, Void.class);
    }
}