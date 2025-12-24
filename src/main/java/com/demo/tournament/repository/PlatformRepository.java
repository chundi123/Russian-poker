package com.demo.tournament.repository;

import com.demo.tournament.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlatformRepository extends JpaRepository<Platform, Long> {

    Optional<Platform> findByPlatformCode(String platformCode);

    Optional<Platform> findByIdAndStatus(Long id, String status);
}
