package com.biodiversity.demo.service;

import com.biodiversity.demo.config.SupabaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SupabaseService {

    @Autowired
    protected SupabaseConfig supabaseConfig;

    @Autowired
    protected RestTemplate restTemplate;

    protected HttpHeaders createHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);
        headers.set("apikey", supabaseConfig.getSupabaseAnonKey());
        headers.set("Content-Type", "application/json");
        return headers;
    }

    protected <T> ResponseEntity<T> executeRequest(String url, HttpMethod method, HttpHeaders headers, Object body,
            Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(
                supabaseConfig.getSupabaseUrl() + url,
                method,
                entity,
                responseType);
    }
}