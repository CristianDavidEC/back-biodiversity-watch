package com.biodiversity.demo.service;

import com.biodiversity.demo.config.SupabaseConfig;
import com.biodiversity.demo.model.Specie;
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

class SpecieServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SupabaseConfig supabaseConfig;

    @InjectMocks
    private SpecieService specieService;

    private static final String TEST_AUTH_TOKEN = "test-token";
    private static final String TEST_SUPABASE_URL = "http://test.supabase.co";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(specieService, "restTemplate", restTemplate);
        when(supabaseConfig.getSupabaseUrl()).thenReturn(TEST_SUPABASE_URL);
    }

    @Test
    void getAllSpecies_ShouldReturnSpecies() {
        // Arrange
        List<Specie> expectedSpecies = Arrays.asList(
                new Specie(),
                new Specie());
        ResponseEntity<List<Specie>> mockResponse = new ResponseEntity<>(expectedSpecies, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(new ParameterizedTypeReference<List<Specie>>() {
                }.getClass()))).thenReturn(mockResponse);

        // Act
        ResponseEntity<List<Specie>> response = specieService.getAllSpecies(TEST_AUTH_TOKEN);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSpecies, response.getBody());
    }

    @Test
    void getSpecieById_ShouldReturnSpecie() {
        // Arrange
        String id = "123";
        List<Specie> expectedSpecies = Arrays.asList(new Specie());
        ResponseEntity<List<Specie>> mockResponse = new ResponseEntity<>(expectedSpecies, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(new ParameterizedTypeReference<List<Specie>>() {
                }.getClass()))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Specie> response = specieService.getSpecieById(TEST_AUTH_TOKEN, id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSpecies.get(0), response.getBody());
    }

    @Test
    void createSpecie_ShouldReturnCreatedSpecie() {
        // Arrange
        Specie specie = new Specie();
        ResponseEntity<Specie> mockResponse = new ResponseEntity<>(specie, HttpStatus.CREATED);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Specie.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Specie> response = specieService.createSpecie(TEST_AUTH_TOKEN, specie);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(specie, response.getBody());
    }

    @Test
    void updateSpecie_ShouldReturnUpdatedSpecie() {
        // Arrange
        String id = "123";
        Specie specie = new Specie();
        List<Specie> expectedSpecies = Arrays.asList(specie);
        ResponseEntity<List<Specie>> mockResponse = new ResponseEntity<>(expectedSpecies, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                any(new ParameterizedTypeReference<List<Specie>>() {
                }.getClass()))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Specie> response = specieService.updateSpecie(TEST_AUTH_TOKEN, id, specie);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(specie, response.getBody());
    }

    @Test
    void deleteSpecie_ShouldReturnOk() {
        // Arrange
        String id = "123";
        List<Specie> expectedSpecies = Arrays.asList(new Specie());
        ResponseEntity<List<Specie>> mockResponse = new ResponseEntity<>(expectedSpecies, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                any(new ParameterizedTypeReference<List<Specie>>() {
                }.getClass()))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Void> response = specieService.deleteSpecie(TEST_AUTH_TOKEN, id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getSpecieByScientificName_ShouldReturnSpecie() {
        // Arrange
        String scientificName = "Testus scientificus";
        List<Specie> expectedSpecies = Arrays.asList(new Specie());
        ResponseEntity<List<Specie>> mockResponse = new ResponseEntity<>(expectedSpecies, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(new ParameterizedTypeReference<List<Specie>>() {
                }.getClass()))).thenReturn(mockResponse);

        // Act
        ResponseEntity<List<Specie>> response = specieService.getSpecieByScientificName(TEST_AUTH_TOKEN,
                scientificName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSpecies, response.getBody());
    }
}