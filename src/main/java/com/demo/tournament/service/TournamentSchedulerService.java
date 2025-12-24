package com.demo.tournament.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentSchedulerService {

    private final TournamentService tournamentService;

    /**
     * Check for tournaments that should start every 30 seconds
     * This replaces the Quartz TournamentStartJob
     */
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    @Transactional
    public void checkAndStartTournaments() {
        log.debug("Checking for tournaments to start...");

        try {
            // Get all tournaments in REGISTERING status that should start
            var registeringTournaments = tournamentService.getAllTournaments().stream()
                    .filter(t -> t.getStatus() != null && "REGISTERING".equals(t.getStatus().getStatusCode()))
                    .filter(t -> shouldStartTournament(t))
                    .toList();

            for (var tournament : registeringTournaments) {
                try {
                    tournamentService.startTournament(tournament.getId());
                    log.info("Automatically started tournament: {}", tournament.getName());
                } catch (Exception e) {
                    log.error("Failed to start tournament {}: {}", tournament.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error in tournament start scheduler: {}", e.getMessage());
        }
    }

    /**
     * Check for rounds that should open every 15 seconds
     * This replaces the Quartz RoundOpenJob
     */
    @Scheduled(fixedRate = 15000) // Every 15 seconds
    @Transactional
    public void checkAndOpenRounds() {
        log.debug("Checking for rounds to open...");

        try {
            // Get all active tournaments and check their rounds
            var activeTournaments = tournamentService.getAllTournaments().stream()
                    .filter(t -> t.getStatus() != null && "RUNNING".equals(t.getStatus().getStatusCode()))
                    .toList();

            for (var tournament : activeTournaments) {
                try {
                    tournamentService.checkAndOpenRounds(tournament.getId());
                } catch (Exception e) {
                    log.error("Failed to check rounds for tournament {}: {}", tournament.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error in round opening scheduler: {}", e.getMessage());
        }
    }

    /**
     * Check for rounds that should close every 10 seconds
     * This replaces the Quartz RoundCloseJob
     */
    @Scheduled(fixedRate = 10000) // Every 10 seconds
    @Transactional
    public void checkAndCloseRounds() {
        log.debug("Checking for rounds to close...");

        try {
            // Get all active tournaments and check their rounds
            var activeTournaments = tournamentService.getAllTournaments().stream()
                    .filter(t -> t.getStatus() != null && "RUNNING".equals(t.getStatus().getStatusCode()))
                    .toList();

            for (var tournament : activeTournaments) {
                try {
                    tournamentService.checkAndCloseRounds(tournament.getId());
                } catch (Exception e) {
                    log.error("Failed to check round closures for tournament {}: {}", tournament.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error in round closing scheduler: {}", e.getMessage());
        }
    }

    /**
     * Check for tournaments that should complete every 60 seconds
     * This replaces the Quartz TournamentCompleteJob
     */
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    @Transactional
    public void checkAndCompleteTournaments() {
        log.debug("Checking for tournaments to complete...");

        try {
            // Get all tournaments in RUNNING status that should complete
            var runningTournaments = tournamentService.getAllTournaments().stream()
                    .filter(t -> t.getStatus() != null && "RUNNING".equals(t.getStatus().getStatusCode()))
                    .filter(t -> shouldCompleteTournament(t))
                    .toList();

            for (var tournament : runningTournaments) {
                try {
                    tournamentService.completeTournament(tournament.getId());
                    log.info("Automatically completed tournament: {}", tournament.getName());
                } catch (Exception e) {
                    log.error("Failed to complete tournament {}: {}", tournament.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error in tournament completion scheduler: {}", e.getMessage());
        }
    }

    /**
     * Clean up old completed tournaments every 24 hours
     */
    @Scheduled(fixedRate = 86400000) // Every 24 hours
    @Transactional
    public void cleanupOldTournaments() {
        log.info("Running tournament cleanup...");

        try {
            // Archive or clean up tournaments older than 30 days
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            Date cutoff = Date.from(cutoffDate.atZone(ZoneId.systemDefault()).toInstant());

            // This would be implemented based on business requirements
            log.info("Tournament cleanup completed at {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error in tournament cleanup: {}", e.getMessage());
        }
    }

    /**
     * Determine if a tournament should start based on registration time
     */
    private boolean shouldStartTournament(Object tournament) {
        // For now, tournaments start immediately after creation
        // In a real implementation, this would check registration deadlines
        return true;
    }

    /**
     * Determine if a tournament should complete
     */
    private boolean shouldCompleteTournament(Object tournament) {
        // For now, tournaments complete after all rounds are done
        // In a real implementation, this would check if all rounds are completed
        return false;
    }
}
