package com.demo.tournament.job;

import com.demo.tournament.entity.Tournament;
import com.demo.tournament.entity.TournamentRound;
import com.demo.tournament.repository.TournamentRepository;
import com.demo.tournament.repository.TournamentRoundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class RoundOpenJob implements Job {

    private final TournamentRepository tournamentRepository;
    private final TournamentRoundRepository tournamentRoundRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long tournamentId = context.getJobDetail().getJobDataMap().getLong("tournamentId");
        Integer roundNumber = context.getJobDetail().getJobDataMap().getInt("roundNumber");
        log.info("RoundOpenJob: Starting execution for tournament ID: {}, Round: {}", tournamentId, roundNumber);

        try {
            Tournament tournament = tournamentRepository.findById(tournamentId)
                    .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id: " + tournamentId));

            String currentStatusCode = tournament.getStatus() != null ? tournament.getStatus().getStatusCode() : null;
            if (!"RUNNING".equals(currentStatusCode)) {
                log.warn("RoundOpenJob: Tournament is not in RUNNING status. Current status: {}, Tournament ID: {}", 
                        currentStatusCode, tournamentId);
                throw new IllegalStateException("Tournament must be in RUNNING status to open rounds");
            }

            TournamentRound round = tournamentRoundRepository.findByTournamentAndRoundNumber(tournament, roundNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Round not found: tournament=" + tournamentId + ", round=" + roundNumber));

            if ("OPEN".equals(round.getStatus())) {
                log.warn("RoundOpenJob: Round {} for tournament {} is already OPEN", roundNumber, tournamentId);
                return;
            }

            round.setStatus("OPEN");
            round.setRoundStartTime(Instant.now());
            tournamentRoundRepository.save(round);

            tournament.setCurrentRound(roundNumber);
            tournamentRepository.save(tournament);

            log.info("RoundOpenJob: Successfully opened round {} for tournament {}", roundNumber, tournamentId);
        } catch (Exception e) {
            log.error("RoundOpenJob: Error executing job for tournament ID: {}, Round: {}", tournamentId, roundNumber, e);
            throw new JobExecutionException("Failed to open round", e);
        }
    }
}

