package com.demo.tournament.repository;

import com.demo.tournament.entity.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentStatusRepository extends JpaRepository<TournamentStatus, Short> {
    Optional<TournamentStatus> findByStatusCode(String statusCode);
}

