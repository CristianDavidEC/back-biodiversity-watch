package com.biodiversity.demo.service;

import com.biodiversity.demo.config.SupabaseConfig;
import com.biodiversity.demo.dto.CreateObservationDTO;
import com.biodiversity.demo.model.Observation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ObservationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SupabaseConfig supabaseConfig;

    @InjectMocks
    private ObservationService observationService;

    private static final String TEST_AUTH_TOKEN = "test-token";
    private static final String TEST_SUPABASE_URL = "http://test.supabase.co";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(observationService, "restTemplate", restTemplate);
        when(supabaseConfig.getSupabaseUrl()).thenReturn(TEST_SUPABASE_URL);
    }

    @Test
    void getAllObservations_ShouldReturnObservations() {
        // Arrange
        int page = 1;
        String specieCommonName = "pato";
        List<Observation> expectedObservations = Arrays.asList(
                new Observation(),
                new Observation());
        ResponseEntity<List<Observation>> mockResponse = new ResponseEntity<>(expectedObservations, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(new ParameterizedTypeReference<List<Observation>>() {
                }.getClass()))).thenReturn(mockResponse);

        // Act
        ResponseEntity<List<Observation>> response = observationService.getAllObservations(TEST_AUTH_TOKEN, page,
                specieCommonName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedObservations, response.getBody());
    }

    @Test
    void getObservationById_ShouldReturnObservation() {
        // Arrange
        String id = "123";
        List<Observation> expectedObservations = Arrays.asList(new Observation());
        ResponseEntity<List<Observation>> mockResponse = new ResponseEntity<>(expectedObservations, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(new ParameterizedTypeReference<List<Observation>>() {
                }.getClass()))).thenReturn(mockResponse);

        // Act
        ResponseEntity<List<Observation>> response = observationService.getObservationById(TEST_AUTH_TOKEN, id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedObservations, response.getBody());
    }

    @Test
    void createObservation_ShouldReturnCreatedObservation() {
        // Arrange
        CreateObservationDTO dto = new CreateObservationDTO();
        List<Observation> expectedObservations = Arrays.asList(new Observation());
        ResponseEntity<List<Observation>> mockResponse = new ResponseEntity<>(expectedObservations, HttpStatus.CREATED);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(new ParameterizedTypeReference<List<Observation>>() {
                }.getClass()))).thenReturn(mockResponse);

        // Act
        ResponseEntity<List<Observation>> response = observationService.createObservation(TEST_AUTH_TOKEN, dto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedObservations, response.getBody());
    }

    @Test
    void updateObservation_ShouldReturnUpdatedObservation() {
        // Arrange
        String id = "123";
        Observation observation = new Observation();
        ResponseEntity<Observation> mockResponse = new ResponseEntity<>(observation, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Observation.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Observation> response = observationService.updateObservation(TEST_AUTH_TOKEN, id, observation);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(observation, response.getBody());
    }

    @Test
    void deleteObservation_ShouldReturnNoContent() {
        // Arrange
        String id = "123";
        ResponseEntity<Void> mockResponse = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Void.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Void> response = observationService.deleteObservation(TEST_AUTH_TOKEN, id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}