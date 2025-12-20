package com.demo.tournament.repository;

import com.demo.tournament.entity.Site;
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
            LEFT JOIN FETCH t.site
            WHERE t.site.id = :siteId
            ORDER BY t.createdAt DESC
            """)
    List<Tournament> findBySiteIdOrderByCreatedAtDesc(@Param("siteId") Long siteId);

    List<Tournament> findBySite(Site site);

    List<Tournament> findBySiteAndStatus(Site site, TournamentStatus status);

    Optional<Tournament> findByIdAndSite(Long id, Site site);
}


