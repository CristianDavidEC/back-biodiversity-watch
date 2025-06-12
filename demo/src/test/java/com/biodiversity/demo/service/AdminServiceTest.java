package com.biodiversity.demo.service;

import com.biodiversity.demo.config.SupabaseConfig;
import com.biodiversity.demo.model.Admin;
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

class AdminServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SupabaseConfig supabaseConfig;

    @InjectMocks
    private AdminService adminService;

    private static final String TEST_AUTH_TOKEN = "test-token";
    private static final String TEST_SUPABASE_URL = "http://test.supabase.co";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(adminService, "restTemplate", restTemplate);
        when(supabaseConfig.getSupabaseUrl()).thenReturn(TEST_SUPABASE_URL);
    }

    @Test
    void getAllAdmins_ShouldReturnAdmins() {
        // Arrange
        List<Admin> expectedAdmins = Arrays.asList(
                new Admin(),
                new Admin());
        ResponseEntity<List<Admin>> mockResponse = new ResponseEntity<>(expectedAdmins, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(new ParameterizedTypeReference<List<Admin>>() {
                }.getClass()))).thenReturn(mockResponse);

        // Act
        ResponseEntity<List<Admin>> response = adminService.getAllAdmins(TEST_AUTH_TOKEN);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAdmins, response.getBody());
    }

    @Test
    void getAdminById_ShouldReturnAdmin() {
        // Arrange
        String id = "123";
        Admin expectedAdmin = new Admin();
        ResponseEntity<Admin> mockResponse = new ResponseEntity<>(expectedAdmin, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Admin.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Admin> response = adminService.getAdminById(TEST_AUTH_TOKEN, id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAdmin, response.getBody());
    }

    @Test
    void getAdminByEmail_ShouldReturnAdmin() {
        // Arrange
        String email = "admin@example.com";
        Admin expectedAdmin = new Admin();
        ResponseEntity<Admin> mockResponse = new ResponseEntity<>(expectedAdmin, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Admin.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Admin> response = adminService.getAdminByEmail(TEST_AUTH_TOKEN, email);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAdmin, response.getBody());
    }

    @Test
    void createAdmin_ShouldReturnCreatedAdmin() {
        // Arrange
        Admin admin = new Admin();
        ResponseEntity<Admin> mockResponse = new ResponseEntity<>(admin, HttpStatus.CREATED);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Admin.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Admin> response = adminService.createAdmin(TEST_AUTH_TOKEN, admin);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(admin, response.getBody());
    }

    @Test
    void updateAdmin_ShouldReturnUpdatedAdmin() {
        // Arrange
        String id = "123";
        Admin admin = new Admin();
        ResponseEntity<Admin> mockResponse = new ResponseEntity<>(admin, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Admin.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Admin> response = adminService.updateAdmin(TEST_AUTH_TOKEN, id, admin);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(admin, response.getBody());
    }

    @Test
    void deleteAdmin_ShouldReturnNoContent() {
        // Arrange
        String id = "123";
        ResponseEntity<Void> mockResponse = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Void.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Void> response = adminService.deleteAdmin(TEST_AUTH_TOKEN, id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}