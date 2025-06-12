package com.biodiversity.demo.service;

import com.biodiversity.demo.config.SupabaseConfig;
import com.biodiversity.demo.model.Profile;
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

class ProfileServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SupabaseConfig supabaseConfig;

    @InjectMocks
    private ProfileService profileService;

    private static final String TEST_AUTH_TOKEN = "test-token";
    private static final String TEST_SUPABASE_URL = "http://test.supabase.co";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(profileService, "restTemplate", restTemplate);
        when(supabaseConfig.getSupabaseUrl()).thenReturn(TEST_SUPABASE_URL);
    }

    @Test
    void getAllProfiles_ShouldReturnProfiles() {
        // Arrange
        List<Profile> expectedProfiles = Arrays.asList(
                new Profile(),
                new Profile());
        ResponseEntity<List<Profile>> mockResponse = new ResponseEntity<>(expectedProfiles, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(new ParameterizedTypeReference<List<Profile>>() {
                }.getClass()))).thenReturn(mockResponse);

        // Act
        ResponseEntity<List<Profile>> response = profileService.getAllProfiles(TEST_AUTH_TOKEN);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProfiles, response.getBody());
    }

    @Test
    void getProfileById_ShouldReturnProfile() {
        // Arrange
        String id = "123";
        List<Profile> expectedProfiles = Arrays.asList(new Profile());
        ResponseEntity<List<Profile>> mockResponse = new ResponseEntity<>(expectedProfiles, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(new ParameterizedTypeReference<List<Profile>>() {
                }.getClass()))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Profile> response = profileService.getProfileById(TEST_AUTH_TOKEN, id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProfiles.get(0), response.getBody());
    }

    @Test
    void getProfileByEmail_ShouldReturnProfile() {
        // Arrange
        String email = "test@example.com";
        List<Profile> expectedProfiles = Arrays.asList(new Profile());
        ResponseEntity<List<Profile>> mockResponse = new ResponseEntity<>(expectedProfiles, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(new ParameterizedTypeReference<List<Profile>>() {
                }.getClass()))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Profile> response = profileService.getProfileByEmail(TEST_AUTH_TOKEN, email);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProfiles.get(0), response.getBody());
    }

    @Test
    void createProfile_ShouldReturnCreatedProfile() {
        // Arrange
        Profile profile = new Profile();
        ResponseEntity<Profile> mockResponse = new ResponseEntity<>(profile, HttpStatus.CREATED);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Profile.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Profile> response = profileService.createProfile(TEST_AUTH_TOKEN, profile);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(profile, response.getBody());
    }

    @Test
    void updateProfile_ShouldReturnUpdatedProfile() {
        // Arrange
        String id = "123";
        Profile profile = new Profile();
        ResponseEntity<Profile> mockResponse = new ResponseEntity<>(profile, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Profile.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Profile> response = profileService.updateProfile(TEST_AUTH_TOKEN, id, profile);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profile, response.getBody());
    }

    @Test
    void deleteProfile_ShouldReturnNoContent() {
        // Arrange
        String id = "123";
        ResponseEntity<Void> mockResponse = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Void.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Void> response = profileService.deleteProfile(TEST_AUTH_TOKEN, id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}