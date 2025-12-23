package com.demo.tournament.service;

import com.demo.tournament.dto.LeaderboardRowDto;
import com.demo.tournament.dto.LobbyTournamentDto;
import com.demo.tournament.dto.RoundResultRequest;
import com.demo.tournament.entity.*;
import com.demo.tournament.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.quartz.SchedulerException;

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
    private final TournamentStatusRepository tournamentStatusRepository;
    private final SiteRepository siteRepository;
    private final TournamentSchedulerService tournamentSchedulerService;

    public TournamentService(TournamentRepository tournamentRepository,
                             AccountRepository accountRepository,
                             TournamentPlayerRepository tournamentPlayerRepository,
                             TournamentRoundRepository tournamentRoundRepository,
                             RoundResultRepository roundResultRepository,
                             TournamentStatusRepository tournamentStatusRepository,
                             SiteRepository siteRepository,
                             TournamentSchedulerService tournamentSchedulerService) {
        this.tournamentRepository = tournamentRepository;
        this.accountRepository = accountRepository;
        this.tournamentPlayerRepository = tournamentPlayerRepository;
        this.tournamentRoundRepository = tournamentRoundRepository;
        this.roundResultRepository = roundResultRepository;
        this.tournamentStatusRepository = tournamentStatusRepository;
        this.siteRepository = siteRepository;
        this.tournamentSchedulerService = tournamentSchedulerService;
    }

    @Transactional
    public Tournament createTournament(Tournament tournament) {
        tournament.setId(null);
        
        // Validate site exists if provided
        if (tournament.getSite() != null && tournament.getSite().getId() != null) {
            Site site = siteRepository.findById(tournament.getSite().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Site not found"));
            tournament.setSite(site);
        }
        
        // If status is not set, default to CREATED
        if (tournament.getStatus() == null) {
            tournamentStatusRepository.findByStatusCode("CREATED")
                    .ifPresent(tournament::setStatus);
        }
        
        // Validate tournament type
        if (tournament.getTournamentType() == null) {
            tournament.setTournamentType("PVD");
        } else if (!tournament.getTournamentType().equals("PVD") && !tournament.getTournamentType().equals("PVP")) {
            throw new IllegalArgumentException("Tournament type must be PVD or PVP");
        }

        // Validate time sequence
        if (tournament.getRegistrationStartTime() != null && tournament.getRegistrationEndTime() != null) {
            if (tournament.getRegistrationStartTime().isAfter(tournament.getRegistrationEndTime())) {
                throw new IllegalArgumentException("Registration start time must be before registration end time");
            }
        }
        if (tournament.getRegistrationEndTime() != null && tournament.getTournamentStartTime() != null) {
            if (tournament.getRegistrationEndTime().isAfter(tournament.getTournamentStartTime())) {
                throw new IllegalArgumentException("Registration end time must be before tournament start time");
            }
        }

        Tournament savedTournament = tournamentRepository.save(tournament);

        // Schedule jobs if times are provided
        try {
            if (savedTournament.getRegistrationStartTime() != null) {
                tournamentSchedulerService.scheduleRegistrationOpen(savedTournament.getId(), savedTournament.getRegistrationStartTime());
            }
            if (savedTournament.getTournamentStartTime() != null) {
                tournamentSchedulerService.scheduleTournamentStart(savedTournament.getId(), savedTournament.getTournamentStartTime());
                // Schedule rounds if totalRounds is specified
                if (savedTournament.getTotalRounds() != null && savedTournament.getTotalRounds() > 0) {
                    tournamentSchedulerService.scheduleRounds(savedTournament.getId(), savedTournament.getTotalRounds(), savedTournament.getTournamentStartTime());
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to schedule tournament jobs: " + e.getMessage(), e);
        }
        
        return savedTournament;
    }

    public Optional<Tournament> getTournamentById(Long id) {
        return tournamentRepository.findById(id);
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    public List<LobbyTournamentDto> lobbyForSite(Long siteId) {
        List<Tournament> tournaments = tournamentRepository.findBySiteIdOrderByCreatedAtDesc(siteId);
        List<LobbyTournamentDto> result = new ArrayList<>();
        for (Tournament t : tournaments) {
            long joined = tournamentPlayerRepository.countByTournamentId(t.getId());
            String statusCode = t.getStatus() != null ? t.getStatus().getStatusCode() : null;
            result.add(new LobbyTournamentDto(
                    t.getId(),
                    t.getName(),
                    t.getStartingChips(),
                    t.getTotalRounds(),
                    t.getMaxPlayers(),
                    statusCode,
                    joined,
                    t.getTournamentType()
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
        Optional<TournamentPlayer> existing = tournamentPlayerRepository
                .findByTournamentAndPlayer(tournament, player);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Check max players limit
        long currentPlayers = tournamentPlayerRepository.countByTournamentId(tournamentId);
        if (tournament.getMaxPlayers() != null && currentPlayers >= tournament.getMaxPlayers()) {
            throw new IllegalStateException("Tournament is full");
        }

        TournamentPlayer tp = new TournamentPlayer();
        tp.setTournament(tournament);
        tp.setPlayer(player);
        Integer startChips = tournament.getStartingChips() != null ? tournament.getStartingChips() : 1000;
        tp.setChipsStart(startChips);
        tp.setChipsCurrent(startChips);
        tp.setChipsReserved(0);
        
        return tournamentPlayerRepository.save(tp);
    }

    public List<LeaderboardRowDto> leaderboard(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        List<TournamentPlayer> players = tournamentPlayerRepository
                .findByTournamentOrderByChipsCurrentDesc(tournament);

        List<LeaderboardRowDto> result = new ArrayList<>();
        for (TournamentPlayer tp : players) {
            result.add(new LeaderboardRowDto(
                    tp.getPlayer().getUsername(),
                    tp.getChipsCurrent(),
                    tp.getTotalWins(),
                    tp.getTotalLosses()
            ));
        }
        return result;
    }

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

        // Check if result already exists
        Optional<RoundResult> existingResult = roundResultRepository.findByRoundAndPlayer(round, player);
        if (existingResult.isPresent()) {
            throw new IllegalStateException("Round result already recorded for this player");
        }

        Integer betChips = request.getBetChips();
        String resultCode = request.getResult();
        Integer delta;
        switch (resultCode) {
            case "WIN" -> delta = betChips;
            case "LOSE" -> delta = -betChips;
            case "PUSH" -> delta = 0;
            default -> throw new IllegalArgumentException("Invalid result: " + resultCode + ". Must be WIN, LOSE, or PUSH");
        }

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
    public Tournament updateTournament(Long id, Tournament tournamentUpdate) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
        
        if (tournamentUpdate.getName() != null) {
            tournament.setName(tournamentUpdate.getName());
        }
        if (tournamentUpdate.getStartingChips() != null) {
            tournament.setStartingChips(tournamentUpdate.getStartingChips());
        }
        if (tournamentUpdate.getTotalRounds() != null) {
            tournament.setTotalRounds(tournamentUpdate.getTotalRounds());
        }
        if (tournamentUpdate.getMaxPlayers() != null) {
            tournament.setMaxPlayers(tournamentUpdate.getMaxPlayers());
        }
        if (tournamentUpdate.getStatus() != null) {
            tournament.setStatus(tournamentUpdate.getStatus());
        }
        if (tournamentUpdate.getTournamentType() != null) {
            if (!tournamentUpdate.getTournamentType().equals("PVD") && !tournamentUpdate.getTournamentType().equals("PVP")) {
                throw new IllegalArgumentException("Tournament type must be PVD or PVP");
            }
            tournament.setTournamentType(tournamentUpdate.getTournamentType());
        }
        
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public void cancelTournament(Long id) throws SchedulerException {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        TournamentStatus cancelledStatus = tournamentStatusRepository.findByStatusCode("CANCELLED")
                .orElseThrow(() -> new IllegalStateException("CANCELLED status not found in database"));

        tournament.setStatus(cancelledStatus);
        tournamentRepository.save(tournament);

        // Cancel all scheduled jobs
        tournamentSchedulerService.cancelScheduledJobs(id);
    }
}


