package com.demo.tournament.service;

import com.demo.tournament.dto.LeaderboardRowDto;
import com.demo.tournament.dto.LobbyTournamentDto;
import com.demo.tournament.dto.RoundResultRequest;
import com.demo.tournament.entity.*;
import com.demo.tournament.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final AccountRepository accountRepository;
    private final TournamentPlayerRepository tournamentPlayerRepository;
    private final TournamentRoundRepository tournamentRoundRepository;
    private final RoundResultRepository roundResultRepository;

    public TournamentService(TournamentRepository tournamentRepository,
                             AccountRepository accountRepository,
                             TournamentPlayerRepository tournamentPlayerRepository,
                             TournamentRoundRepository tournamentRoundRepository,
                             RoundResultRepository roundResultRepository) {
        this.tournamentRepository = tournamentRepository;
        this.accountRepository = accountRepository;
        this.tournamentPlayerRepository = tournamentPlayerRepository;
        this.tournamentRoundRepository = tournamentRoundRepository;
        this.roundResultRepository = roundResultRepository;
    }

    @Transactional
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }
    
    @Transactional
    public Tournament updateTournament(Long id, Tournament update) {
        Tournament t = tournamentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
        if (update.getName() != null) t.setName(update.getName());
        // Other fields as needed...
        return tournamentRepository.save(t);
    }
    
    public Optional<Tournament> getTournamentById(Long id) {
        return tournamentRepository.findById(id);
    }
    
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    // --- FIX: Implement missing Tournament methods for controller ---

    public List<LobbyTournamentDto> lobbyForSite(Long siteId) {
        // This is just a stub/sample implementation. Customize as needed.
        List<Tournament> tournaments = tournamentRepository.findAll();
        List<LobbyTournamentDto> result = new ArrayList<>();
        for (Tournament t : tournaments) {
            // populate DTO with what you need
            result.add(new LobbyTournamentDto(t.getId(), t.getName(), t.getStartingChips(), t.getTotalRounds(), t.getMaxPlayers(), t.getStatus() != null ? t.getStatus().getStatusCode() : null, 0L, t.getTournamentType()));
        }
        return result;
    }

    public List<LeaderboardRowDto> leaderboard(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
        List<TournamentPlayer> players = tournamentPlayerRepository
                .findByTournamentOrderByChipsCurrentDesc(tournament);
        List<LeaderboardRowDto> result = new ArrayList<>();
        for (TournamentPlayer tp : players) {
            result.add(new LeaderboardRowDto(
                tp.getPlayer() != null ? tp.getPlayer().getUsername() : null,
                tp.getChipsCurrent(), tp.getTotalWins(), tp.getTotalLosses()
            ));
        }
        return result;
    }

    @Transactional
    public TournamentPlayer joinTournament(Long tournamentId, Long playerId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
        Account player = accountRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        // Check if already joined
        Optional<TournamentPlayer> existing = tournamentPlayerRepository.findByTournamentAndPlayer(tournament, player);
        if (existing.isPresent()) {
            return existing.get();
        }
        TournamentPlayer tp = new TournamentPlayer();
        tp.setTournament(tournament);
        tp.setPlayer(player);
        tp.setChipsStart(1000); // default start chips
        tp.setChipsCurrent(1000);
        tp.setChipsReserved(0);
        return tournamentPlayerRepository.save(tp);
    }
    // -- END implementations --

    @Transactional
    public RoundResult recordRoundResult(RoundResultRequest request) {
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
        Account player = accountRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        TournamentPlayer tp = tournamentPlayerRepository
                .findByTournamentAndPlayer(tournament, player)
                .orElseThrow(() -> new IllegalStateException("Player not joined in tournament"));
        TournamentRound round = tournamentRoundRepository
                .findByTournamentAndRoundNumber(tournament, request.getRoundNumber())
                .orElseGet(() -> {
                    TournamentRound r = new TournamentRound();
                    r.setTournament(tournament);
                    r.setRoundNumber(request.getRoundNumber());
                    r.setStatus("OPEN");
                    return tournamentRoundRepository.save(r);
                });
        Optional<RoundResult> existingResult = roundResultRepository.findByRoundAndPlayer(round, player);
        if (existingResult.isPresent()) {
            throw new IllegalStateException("Round result already recorded for this player");
        }
        Integer betChips = request.getBetChips();
        String resultCode = request.getResult();
        Integer delta = switch (resultCode) {
            case "WIN" -> betChips;
            case "LOSE" -> -betChips;
            case "PUSH" -> 0;
            default -> throw new IllegalArgumentException("Invalid result: " + resultCode + ". Must be WIN, LOSE, or PUSH");
        };
        Integer chipsAfter = tp.getChipsCurrent() + delta;
        if (chipsAfter < 0) {
            throw new IllegalStateException("Insufficient chips. Cannot have negative chips.");
        }
        tp.setChipsCurrent(chipsAfter);
        switch (resultCode) {
            case "WIN" -> tp.setTotalWins(tp.getTotalWins() + 1);
            case "LOSE" -> tp.setTotalLosses(tp.getTotalLosses() + 1);
            case "PUSH" -> tp.setTotalPushes(tp.getTotalPushes() + 1);
        }
        tournamentPlayerRepository.save(tp);
        RoundResult roundResult = new RoundResult();
        roundResult.setRound(round);
        roundResult.setPlayer(player);
        roundResult.setBetChips(betChips);
        roundResult.setChipsDelta(delta);
        roundResult.setChipsAfter(chipsAfter);
        roundResult.setResult(resultCode);
        return roundResultRepository.save(roundResult);
    }

    @Transactional
    public void cancelTournament(Long id) {
        // Minimal stub for controller's cancelTournament use; implement as needed.
        tournamentRepository.deleteById(id);
    }
}
