package com.demo.tournament.repository;

import com.demo.tournament.entity.Tournament;
import com.demo.tournament.entity.TournamentRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentRoundRepository extends JpaRepository<TournamentRound, Long> {

    List<TournamentRound> findByTournamentOrderByRoundNumberAsc(Tournament tournament);

    Optional<TournamentRound> findFirstByTournamentOrderByRoundNumberDesc(Tournament tournament);

    Optional<TournamentRound> findByTournamentAndRoundNumber(Tournament tournament, int roundNumber);
}


