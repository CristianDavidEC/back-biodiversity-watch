package com.biodiversity.demo.controller;

import com.biodiversity.demo.model.Admin;
import com.biodiversity.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins(@RequestHeader("Authorization") String authToken) {
        return adminService.getAllAdmins(authToken);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminById(@RequestHeader("Authorization") String authToken,
            @PathVariable String id) {
        return adminService.getAdminById(authToken, id);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Admin> getAdminByEmail(@RequestHeader("Authorization") String authToken,
            @PathVariable String email) {
        return adminService.getAdminByEmail(authToken, email);
    }

    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestHeader("Authorization") String authToken,
            @RequestBody Admin admin) {
        return adminService.createAdmin(authToken, admin);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Admin> updateAdmin(@RequestHeader("Authorization") String authToken,
            @PathVariable String id,
            @RequestBody Admin admin) {
        return adminService.updateAdmin(authToken, id, admin);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@RequestHeader("Authorization") String authToken,
            @PathVariable String id) {
        return adminService.deleteAdmin(authToken, id);
    }
}