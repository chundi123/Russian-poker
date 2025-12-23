package com.demo.tournament.job;

import com.demo.tournament.entity.Tournament;
import com.demo.tournament.entity.TournamentStatus;
import com.demo.tournament.repository.TournamentRepository;
import com.demo.tournament.repository.TournamentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class TournamentCompleteJob implements Job {

    private final TournamentRepository tournamentRepository;
    private final TournamentStatusRepository tournamentStatusRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long tournamentId = context.getJobDetail().getJobDataMap().getLong("tournamentId");
        log.info("TournamentCompleteJob: Starting execution for tournament ID: {}", tournamentId);

        try {
            Tournament tournament = tournamentRepository.findById(tournamentId)
                    .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id: " + tournamentId));

            String currentStatusCode = tournament.getStatus() != null ? tournament.getStatus().getStatusCode() : null;

            if (!"RUNNING".equals(currentStatusCode)) {
                log.warn("TournamentCompleteJob: Invalid state transition. Current status: {}, Expected: RUNNING. Tournament ID: {}", 
                        currentStatusCode, tournamentId);
                throw new IllegalStateException("Tournament must be in RUNNING status to transition to COMPLETED");
            }

            TournamentStatus completedStatus = tournamentStatusRepository.findByStatusCode("COMPLETED")
                    .orElseThrow(() -> new IllegalStateException("COMPLETED status not found in database"));

            tournament.setStatus(completedStatus);
            tournamentRepository.save(tournament);

            log.info("TournamentCompleteJob: Successfully transitioned tournament {} from RUNNING to COMPLETED", tournamentId);
        } catch (Exception e) {
            log.error("TournamentCompleteJob: Error executing job for tournament ID: {}", tournamentId, e);
            throw new JobExecutionException("Failed to complete tournament", e);
        }
    }
}

