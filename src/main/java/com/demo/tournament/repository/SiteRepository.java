package com.demo.tournament.repository;

import com.demo.tournament.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Long> {

    Optional<Site> findBySiteCode(String siteCode);

    Optional<Site> findByIdAndStatus(Long id, String status);
}


