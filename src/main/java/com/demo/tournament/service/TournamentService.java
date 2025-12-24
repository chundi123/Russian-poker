package com.demo.tournament.service;

import com.demo.tournament.dto.LeaderboardRowDto;
import com.demo.tournament.dto.LobbyTournamentDto;
import com.demo.tournament.dto.PlayerTransactionHistoryDto;
import com.demo.tournament.dto.RoundResultRequest;
import com.demo.tournament.repository.RoundResultRepository;
import com.demo.tournament.repository.TournamentRepository;
import com.demo.tournament.repository.AccountRepository;
import com.demo.tournament.repository.TournamentPlayerRepository;
import com.demo.tournament.repository.TournamentRoundRepository;
import com.demo.tournament.repository.TournamentStatusRepository;
import com.demo.tournament.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final AccountRepository accountRepository;
    private final TournamentPlayerRepository tournamentPlayerRepository;
    private final TournamentRoundRepository tournamentRoundRepository;
    private final RoundResultRepository roundResultRepository;
    private final TournamentStatusRepository tournamentStatusRepository;


    @Transactional
    public Tournament createTournament(Tournament tournament) {
        // Validate tournament data
        System.out.println("Validating tournament data: " + tournament);
        validateTournamentData(tournament);

        // Set default status if not provided
        if (tournament.getStatus() == null) {
            TournamentStatus defaultStatus = tournamentStatusRepository.findByStatusCode("CREATED")
                    .orElseThrow(() -> new IllegalStateException("Default tournament status not found"));
            tournament.setStatus(defaultStatus);
        }

        System.out.println("Validation passed, saving tournament");
        return tournamentRepository.save(tournament);
    }

    private void validateTournamentData(Tournament tournament) {
        if (tournament.getName() == null || tournament.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tournament name is required");
        }
        if (tournament.getName().length() < 3 || tournament.getName().length() > 100) {
            throw new IllegalArgumentException("Tournament name must be between 3 and 100 characters");
        }
        if (tournament.getStartingChips() == null || tournament.getStartingChips() < 100) {
            throw new IllegalArgumentException("Starting chips must be at least 100");
        }
        if (tournament.getStartingChips() > 10000) {
            throw new IllegalArgumentException("Starting chips cannot exceed 10,000");
        }
        if (tournament.getTotalRounds() != null && tournament.getTotalRounds() < 1) {
            throw new IllegalArgumentException("Total rounds must be at least 1");
        }
        if (tournament.getTotalRounds() != null && tournament.getTotalRounds() > 20) {
            throw new IllegalArgumentException("Total rounds cannot exceed 20");
        }
        if (tournament.getMaxPlayers() != null && tournament.getMaxPlayers() < 2) {
            throw new IllegalArgumentException("Maximum players must be at least 2");
        }
        if (tournament.getMaxPlayers() != null && tournament.getMaxPlayers() > 1000) {
            throw new IllegalArgumentException("Maximum players cannot exceed 1,000");
        }
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

    public List<LobbyTournamentDto> lobbyForPlatform(Long platformId) {
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
        tp.setChipsStart(tournament.getStartingChips()); // default start chips
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
        roundResult.setChips(betChips);
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

    // Get comprehensive player transaction history for owner/admin view
    public List<PlayerTransactionHistoryDto> getPlayerTransactionHistory(Long playerId) {
        Account player = accountRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        List<RoundResult> roundResults = roundResultRepository.findByPlayerOrderByRecordedAtDesc(player);
        List<PlayerTransactionHistoryDto> transactionHistory = new ArrayList<>();

        for (RoundResult result : roundResults) {
            TournamentRound round = result.getRound();
            Tournament tournament = round != null ? round.getTournament() : null;

            PlayerTransactionHistoryDto dto = new PlayerTransactionHistoryDto(
                result.getRoundResultId(),
                tournament != null ? tournament.getId() : null,
                tournament != null ? tournament.getName() : "Unknown Tournament",
                round != null ? round.getRoundNumber() : 0,
                result.getChips(),
                result.getResult(),
                result.getChipsDelta(),
                result.getChipsAfter(),
                result.getRecordedAt()
            );

            transactionHistory.add(dto);
        }

        return transactionHistory;
    }

    // Get player transaction summary (totals, statistics)
    public Map<String, Object> getPlayerTransactionSummary(Long playerId) {
        Account player = accountRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        List<RoundResult> roundResults = roundResultRepository.findByPlayerOrderByRecordedAtDesc(player);

        int totalBets = roundResults.size();
        int totalWins = 0;
        int totalLosses = 0;
        int totalPushes = 0;
        int totalBetAmount = 0;
        int totalWinnings = 0;
        int totalLossAmount = 0;

        for (RoundResult result : roundResults) {
            totalBetAmount += result.getChips();

            switch (result.getResult()) {
                case "WIN" -> {
                    totalWins++;
                    totalWinnings += result.getChipsDelta();
                }
                case "LOSE" -> {
                    totalLosses++;
                    totalLossAmount += Math.abs(result.getChipsDelta());
                }
                case "PUSH" -> totalPushes++;
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("playerId", playerId);
        summary.put("playerUsername", player.getUsername());
        summary.put("totalBets", totalBets);
        summary.put("totalWins", totalWins);
        summary.put("totalLosses", totalLosses);
        summary.put("totalPushes", totalPushes);
        summary.put("totalBetAmount", totalBetAmount);
        summary.put("totalWinnings", totalWinnings);
        summary.put("totalLossAmount", totalLossAmount);
        summary.put("netResult", totalWinnings - totalLossAmount);
        summary.put("winRate", totalBets > 0 ? (double) totalWins / totalBets * 100 : 0);

        return summary;
    }

    // Scheduler methods for automatic tournament state management

    @Transactional
    public void startTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        // Change status from REGISTERING to RUNNING
        TournamentStatus runningStatus = tournamentStatusRepository.findByStatusCode("RUNNING")
                .orElseThrow(() -> new IllegalArgumentException("RUNNING status not found"));

        tournament.setStatus(runningStatus);
        tournamentRepository.save(tournament);

        // Create initial rounds if needed
        if (tournament.getTotalRounds() != null && tournament.getTotalRounds() > 0) {
            for (int i = 1; i <= tournament.getTotalRounds(); i++) {
                TournamentRound round = new TournamentRound();
                round.setTournament(tournament);
                round.setRoundNumber(i);
                round.setStatus("CLOSED");
                tournamentRoundRepository.save(round);
            }
        }

        log.info("Tournament {} started successfully", tournament.getName());
    }

    @Transactional
    public void checkAndOpenRounds(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        // Logic to open next round - simplified for now
        // In a real implementation, this would check timing and player readiness
        var closedRounds = tournamentRoundRepository.findByTournamentOrderByRoundNumberAsc(tournament).stream()
                .filter(round -> "CLOSED".equals(round.getStatus()))
                .toList();

        if (!closedRounds.isEmpty()) {
            TournamentRound nextRound = closedRounds.get(0);
            nextRound.setStatus("OPEN");
            tournamentRoundRepository.save(nextRound);
            log.debug("Opened round {} for tournament {}", nextRound.getRoundNumber(), tournament.getName());
        }
    }

    @Transactional
    public void checkAndCloseRounds(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        // Logic to close rounds that should be closed
        var openRounds = tournamentRoundRepository.findByTournamentOrderByRoundNumberAsc(tournament).stream()
                .filter(round -> "OPEN".equals(round.getStatus()))
                .toList();

        for (TournamentRound round : openRounds) {
            // Simplified logic - close rounds immediately for now
            // In real implementation, this would check time limits and completion criteria
            round.setStatus("CLOSED");
            tournamentRoundRepository.save(round);
            log.debug("Closed round {} for tournament {}", round.getRoundNumber(), tournament.getName());
        }
    }

    @Transactional
    public void completeTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        // Change status to COMPLETED
        TournamentStatus completedStatus = tournamentStatusRepository.findByStatusCode("COMPLETED")
                .orElseThrow(() -> new IllegalArgumentException("COMPLETED status not found"));

        tournament.setStatus(completedStatus);
        tournamentRepository.save(tournament);

        // Calculate final rankings and distribute prizes
        // This would be implemented based on business requirements

        log.info("Tournament {} completed successfully", tournament.getName());
    }
}
