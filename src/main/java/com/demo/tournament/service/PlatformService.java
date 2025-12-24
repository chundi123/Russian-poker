package com.demo.tournament.service;

import com.demo.tournament.entity.Platform;
import com.demo.tournament.repository.PlatformRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlatformService {

    private final PlatformRepository platformRepository;

    public PlatformService(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    @Transactional
    public Platform createPlatform(Platform platform) {
        platform.setId(null);
        if (platform.getStatus() == null) {
            platform.setStatus("ACTIVE");
        }
        return platformRepository.save(platform);
    }

    public List<Platform> getAllPlatforms() {
        return platformRepository.findAll();
    }

    public Optional<Platform> getPlatformById(Long id) {
        return platformRepository.findById(id);
    }

    public Optional<Platform> getPlatformByCode(String platformCode) {
        return platformRepository.findByPlatformCode(platformCode);
    }

    @Transactional
    public Platform updatePlatform(Long id, Platform platformUpdate) {
        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Platform not found with id: " + id));

        if (platformUpdate.getPlatformName() != null) {
            platform.setPlatformName(platformUpdate.getPlatformName());
        }
        if (platformUpdate.getStatus() != null) {
            platform.setStatus(platformUpdate.getStatus());
        }

        return platformRepository.save(platform);
    }

    @Transactional
    public void deletePlatform(Long id) {
        if (!platformRepository.existsById(id)) {
            throw new IllegalArgumentException("Platform not found with id: " + id);
        }
        platformRepository.deleteById(id);
    }
}
