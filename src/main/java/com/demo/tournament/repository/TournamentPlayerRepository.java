package com.demo.tournament.repository;

import com.demo.tournament.entity.Account;
import com.demo.tournament.entity.Tournament;
import com.demo.tournament.entity.TournamentPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TournamentPlayerRepository extends JpaRepository<TournamentPlayer, Long> {

    Optional<TournamentPlayer> findByTournamentAndPlayer(Tournament tournament, Account player);

    List<TournamentPlayer> findByTournamentOrderByChipsCurrentDesc(Tournament tournament);

    List<TournamentPlayer> findByPlayer(Account player);

    @Query("""
            SELECT COUNT(tp)
            FROM TournamentPlayer tp
            WHERE tp.tournament.id = :tournamentId
            """)
    long countByTournamentId(@Param("tournamentId") Long tournamentId);

    @Query("""
            SELECT tp
            FROM TournamentPlayer tp
            WHERE tp.tournament.id = :tournamentId
            ORDER BY tp.chipsCurrent DESC, tp.totalWins DESC
            """)
    List<TournamentPlayer> findLeaderboardByTournamentId(@Param("tournamentId") Long tournamentId);
}


