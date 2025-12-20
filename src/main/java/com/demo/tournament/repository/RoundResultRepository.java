package com.demo.tournament.repository;

import com.demo.tournament.entity.Account;
import com.demo.tournament.entity.RoundResult;
import com.demo.tournament.entity.TournamentRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoundResultRepository extends JpaRepository<RoundResult, Long> {

    List<RoundResult> findByRound(TournamentRound round);

    Optional<RoundResult> findByRoundAndPlayer(TournamentRound round, Account player);

    List<RoundResult> findByPlayer(Account player);
}


