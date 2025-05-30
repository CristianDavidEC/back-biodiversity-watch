package com.biodiversity.demo.controller;

import com.biodiversity.demo.model.Profile;
import com.biodiversity.demo.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public ResponseEntity<List<Profile>> getAllProfiles(@RequestHeader("Authorization") String authToken) {
        return profileService.getAllProfiles(authToken);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profile> getProfileById(@RequestHeader("Authorization") String authToken,
            @PathVariable String id) {
        return profileService.getProfileById(authToken, id);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Profile> getProfileByEmail(@RequestHeader("Authorization") String authToken,
            @PathVariable String email) {
        return profileService.getProfileByEmail(authToken, email);
    }

    @PostMapping
    public ResponseEntity<Profile> createProfile(@RequestHeader("Authorization") String authToken,
            @RequestBody Profile profile) {
        return profileService.createProfile(authToken, profile);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Profile> updateProfile(@RequestHeader("Authorization") String authToken,
            @PathVariable String id,
            @RequestBody Profile profile) {
        return profileService.updateProfile(authToken, id, profile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@RequestHeader("Authorization") String authToken,
            @PathVariable String id) {
        return profileService.deleteProfile(authToken, id);
    }
}