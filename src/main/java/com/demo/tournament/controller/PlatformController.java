package com.demo.tournament.controller;

import com.demo.tournament.entity.Platform;
import com.demo.tournament.service.PlatformService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platforms")
@CrossOrigin
public class PlatformController {

    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @PostMapping
    public ResponseEntity<Platform> createPlatform(@RequestBody Platform platform) {
        Platform created = platformService.createPlatform(platform);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Platform>> getAllPlatforms() {
        List<Platform> platforms = platformService.getAllPlatforms();
        return ResponseEntity.ok(platforms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Platform> getPlatformById(@PathVariable Long id) {
        return platformService.getPlatformById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{platformCode}")
    public ResponseEntity<Platform> getPlatformByCode(@PathVariable String platformCode) {
        return platformService.getPlatformByCode(platformCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Platform> updatePlatform(@PathVariable Long id, @RequestBody Platform platform) {
        try {
            Platform updated = platformService.updatePlatform(id, platform);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlatform(@PathVariable Long id) {
        try {
            platformService.deletePlatform(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
