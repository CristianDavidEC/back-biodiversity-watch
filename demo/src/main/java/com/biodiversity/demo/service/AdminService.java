package com.biodiversity.demo.service;

import com.biodiversity.demo.model.Admin;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService extends SupabaseService {

    private static final String ADMINS_ENDPOINT = "/rest/v1/admins";

    public ResponseEntity<List<Admin>> getAllAdmins(String authToken) {
        HttpHeaders headers = createHeaders(authToken);
        return restTemplate.exchange(
                supabaseConfig.getSupabaseUrl() + ADMINS_ENDPOINT,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Admin>>() {
                });
    }

    public ResponseEntity<Admin> getAdminById(String authToken, String id) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(ADMINS_ENDPOINT + "?id=eq." + id, HttpMethod.GET, headers, null, Admin.class);
    }

    public ResponseEntity<Admin> getAdminByEmail(String authToken, String email) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(ADMINS_ENDPOINT + "?email=eq." + email, HttpMethod.GET, headers, null, Admin.class);
    }

    public ResponseEntity<Admin> createAdmin(String authToken, Admin admin) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(ADMINS_ENDPOINT, HttpMethod.POST, headers, admin, Admin.class);
    }

    public ResponseEntity<Admin> updateAdmin(String authToken, String id, Admin admin) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(ADMINS_ENDPOINT + "?id=eq." + id, HttpMethod.PATCH, headers, admin, Admin.class);
    }

    public ResponseEntity<Void> deleteAdmin(String authToken, String id) {
        HttpHeaders headers = createHeaders(authToken);
        return executeRequest(ADMINS_ENDPOINT + "?id=eq." + id, HttpMethod.DELETE, headers, null, Void.class);
    }
}