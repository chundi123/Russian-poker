package com.demo.tournament.job;

import com.demo.tournament.entity.Tournament;
import com.demo.tournament.entity.TournamentRound;
import com.demo.tournament.entity.TournamentStatus;
import com.demo.tournament.repository.TournamentRepository;
import com.demo.tournament.repository.TournamentRoundRepository;
import com.demo.tournament.repository.TournamentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class RoundCloseJob implements Job {

    private final TournamentRepository tournamentRepository;
    private final TournamentRoundRepository tournamentRoundRepository;
    private final TournamentStatusRepository tournamentStatusRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long tournamentId = context.getJobDetail().getJobDataMap().getLong("tournamentId");
        Integer roundNumber = context.getJobDetail().getJobDataMap().getInt("roundNumber");
        log.info("RoundCloseJob: Starting execution for tournament ID: {}, Round: {}", tournamentId, roundNumber);

        try {
            Tournament tournament = tournamentRepository.findById(tournamentId)
                    .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id: " + tournamentId));

            TournamentRound round = tournamentRoundRepository.findByTournamentAndRoundNumber(tournament, roundNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Round not found: tournament=" + tournamentId + ", round=" + roundNumber));

            if (!"OPEN".equals(round.getStatus())) {
                log.warn("RoundCloseJob: Round {} for tournament {} is not OPEN. Current status: {}", 
                        roundNumber, tournamentId, round.getStatus());
                throw new IllegalStateException("Round must be OPEN to close it");
            }

            round.setStatus("CLOSED");
            round.setRoundEndTime(Instant.now());
            tournamentRoundRepository.save(round);

            // Check if this is the last round
            List<TournamentRound> allRounds = tournamentRoundRepository.findByTournamentOrderByRoundNumberAsc(tournament);
            boolean isLastRound = roundNumber.equals(allRounds.get(allRounds.size() - 1).getRoundNumber());

            if (isLastRound) {
                TournamentStatus completedStatus = tournamentStatusRepository.findByStatusCode("COMPLETED")
                        .orElseThrow(() -> new IllegalStateException("COMPLETED status not found in database"));
                tournament.setStatus(completedStatus);
                tournamentRepository.save(tournament);
                log.info("RoundCloseJob: Last round closed. Tournament {} transitioned to COMPLETED", tournamentId);
            }

            log.info("RoundCloseJob: Successfully closed round {} for tournament {}", roundNumber, tournamentId);
        } catch (Exception e) {
            log.error("RoundCloseJob: Error executing job for tournament ID: {}, Round: {}", tournamentId, roundNumber, e);
            throw new JobExecutionException("Failed to close round", e);
        }
    }
}

