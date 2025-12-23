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
public class TournamentRegistrationOpenJob implements Job {

    private final TournamentRepository tournamentRepository;
    private final TournamentStatusRepository tournamentStatusRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long tournamentId = context.getJobDetail().getJobDataMap().getLong("tournamentId");
        log.info("TournamentRegistrationOpenJob: Starting execution for tournament ID: {}", tournamentId);

        try {
            Tournament tournament = tournamentRepository.findById(tournamentId)
                    .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id: " + tournamentId));

            String currentStatusCode = tournament.getStatus() != null ? tournament.getStatus().getStatusCode() : null;

            if (!"CREATED".equals(currentStatusCode)) {
                log.warn("TournamentRegistrationOpenJob: Invalid state transition. Current status: {}, Expected: CREATED. Tournament ID: {}", 
                        currentStatusCode, tournamentId);
                throw new IllegalStateException("Tournament must be in CREATED status to transition to REGISTERING");
            }

            TournamentStatus registeringStatus = tournamentStatusRepository.findByStatusCode("REGISTERING")
                    .orElseThrow(() -> new IllegalStateException("REGISTERING status not found in database"));

            tournament.setStatus(registeringStatus);
            tournamentRepository.save(tournament);

            log.info("TournamentRegistrationOpenJob: Successfully transitioned tournament {} from CREATED to REGISTERING", tournamentId);
        } catch (Exception e) {
            log.error("TournamentRegistrationOpenJob: Error executing job for tournament ID: {}", tournamentId, e);
            throw new JobExecutionException("Failed to open tournament registration", e);
        }
    }
}

