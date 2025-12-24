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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class TournamentStartJob implements Job {

    private final TournamentRepository tournamentRepository;
    private final TournamentStatusRepository tournamentStatusRepository;
    private final TournamentRoundRepository tournamentRoundRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long tournamentId = context.getJobDetail().getJobDataMap().getLong("tournamentId");
        log.info("TournamentStartJob: Starting execution for tournament ID: {}", tournamentId);

        try {
            Tournament tournament = tournamentRepository.findById(tournamentId)
                    .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id: " + tournamentId));

            String currentStatusCode = tournament.getStatus() != null ? tournament.getStatus().getStatusCode() : null;

            if (!"REGISTERING".equals(currentStatusCode)) {
                log.warn("TournamentStartJob: Invalid state transition. Current status: {}, Expected: REGISTERING. Tournament ID: {}", 
                        currentStatusCode, tournamentId);
                throw new IllegalStateException("Tournament must be in REGISTERING status to transition to RUNNING");
            }

            TournamentStatus runningStatus = tournamentStatusRepository.findByStatusCode("RUNNING")
                    .orElseThrow(() -> new IllegalStateException("RUNNING status not found in database"));

            tournament.setStatus(runningStatus);
            tournamentRepository.save(tournament);

            // Create rounds if totalRounds is specified
            if (tournament.getTotalRounds() != null && tournament.getTotalRounds() > 0) {
                List<TournamentRound> rounds = new ArrayList<>();
                for (int i = 1; i <= tournament.getTotalRounds(); i++) {
                    TournamentRound round = new TournamentRound();
                    round.setTournament(tournament);
                    round.setRoundNumber(i);
                    round.setStatus("CLOSED");
                    rounds.add(round);
                }
                tournamentRoundRepository.saveAll(rounds);
                log.info("TournamentStartJob: Created {} rounds for tournament {}", tournament.getTotalRounds(), tournamentId);
            }

            log.info("TournamentStartJob: Successfully transitioned tournament {} from REGISTERING to RUNNING", tournamentId);
        } catch (Exception e) {
            log.error("TournamentStartJob: Error executing job for tournament ID: {}", tournamentId, e);
            throw new JobExecutionException("Failed to start tournament", e);
        }
    }
}

