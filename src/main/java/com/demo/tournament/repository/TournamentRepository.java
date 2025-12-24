package com.demo.tournament.repository;

import com.demo.tournament.entity.Platform;
import com.demo.tournament.entity.Tournament;
import com.demo.tournament.entity.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    @Query("""
            SELECT t
            FROM Tournament t
            LEFT JOIN FETCH t.status
            LEFT JOIN FETCH t.platform
            WHERE t.platform.id = :platformId
            ORDER BY t.createdAt DESC
            """)
    List<Tournament> findByPlatformIdOrderByCreatedAtDesc(@Param("platformId") Long platformId);

    List<Tournament> findByPlatform(Platform platform);

    List<Tournament> findByPlatformAndStatus(Platform platform, TournamentStatus status);

    Optional<Tournament> findByIdAndPlatform(Long id, Platform platform);
}


