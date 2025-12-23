package com.demo.tournament.service;

import com.demo.tournament.job.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentSchedulerService {

    private final Scheduler scheduler;

    @Transactional
    public void scheduleRegistrationOpen(Long tournamentId, Instant registrationStartTime) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(TournamentRegistrationOpenJob.class)
                .withIdentity("registration-open-" + tournamentId, "tournament-jobs")
                .usingJobData("tournamentId", tournamentId)
                .storeDurably(false)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("registration-open-trigger-" + tournamentId, "tournament-triggers")
                .startAt(Date.from(registrationStartTime))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("Scheduled registration open job for tournament {} at {}", tournamentId, registrationStartTime);
    }

    @Transactional
    public void scheduleTournamentStart(Long tournamentId, Instant tournamentStartTime) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(TournamentStartJob.class)
                .withIdentity("tournament-start-" + tournamentId, "tournament-jobs")
                .usingJobData("tournamentId", tournamentId)
                .storeDurably(false)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("tournament-start-trigger-" + tournamentId, "tournament-triggers")
                .startAt(Date.from(tournamentStartTime))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("Scheduled tournament start job for tournament {} at {}", tournamentId, tournamentStartTime);
    }

    @Transactional
    public void scheduleRounds(Long tournamentId, Integer totalRounds, Instant tournamentStartTime) throws SchedulerException {
        if (totalRounds == null || totalRounds <= 0) {
            log.warn("No rounds to schedule for tournament {}", tournamentId);
            return;
        }

        Instant currentRoundStart = tournamentStartTime;
        long roundDurationMinutes = 30; // Default 30 minutes per round, can be made configurable

        for (int roundNumber = 1; roundNumber <= totalRounds; roundNumber++) {
            // Schedule round open
            JobDetail openJobDetail = JobBuilder.newJob(RoundOpenJob.class)
                    .withIdentity("round-open-" + tournamentId + "-" + roundNumber, "round-jobs")
                    .usingJobData("tournamentId", tournamentId)
                    .usingJobData("roundNumber", roundNumber)
                    .storeDurably(false)
                    .build();

            Trigger openTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("round-open-trigger-" + tournamentId + "-" + roundNumber, "round-triggers")
                    .startAt(Date.from(currentRoundStart))
                    .build();

            scheduler.scheduleJob(openJobDetail, openTrigger);

            // Schedule round close
            Instant roundEndTime = currentRoundStart.plus(roundDurationMinutes, ChronoUnit.MINUTES);

            JobDetail closeJobDetail = JobBuilder.newJob(RoundCloseJob.class)
                    .withIdentity("round-close-" + tournamentId + "-" + roundNumber, "round-jobs")
                    .usingJobData("tournamentId", tournamentId)
                    .usingJobData("roundNumber", roundNumber)
                    .storeDurably(false)
                    .build();

            Trigger closeTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("round-close-trigger-" + tournamentId + "-" + roundNumber, "round-triggers")
                    .startAt(Date.from(roundEndTime))
                    .build();

            scheduler.scheduleJob(closeJobDetail, closeTrigger);

            log.info("Scheduled round {} for tournament {}: Open at {}, Close at {}", 
                    roundNumber, tournamentId, currentRoundStart, roundEndTime);

            // Next round starts immediately after previous round closes
            currentRoundStart = roundEndTime;
        }
    }

    @Transactional
    public void scheduleTournamentCompletion(Long tournamentId, Instant completionTime) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(TournamentCompleteJob.class)
                .withIdentity("tournament-complete-" + tournamentId, "tournament-jobs")
                .usingJobData("tournamentId", tournamentId)
                .storeDurably(false)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("tournament-complete-trigger-" + tournamentId, "tournament-triggers")
                .startAt(Date.from(completionTime))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("Scheduled tournament completion job for tournament {} at {}", tournamentId, completionTime);
    }

    public void cancelScheduledJobs(Long tournamentId) throws SchedulerException {
        // Cancel all jobs for this tournament
        String[] jobGroups = {"tournament-jobs", "round-jobs"};
        String[] triggerGroups = {"tournament-triggers", "round-triggers"};

        for (String group : jobGroups) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group))) {
                if (jobKey.getName().contains(tournamentId.toString())) {
                    scheduler.deleteJob(jobKey);
                    log.info("Deleted job: {}", jobKey);
                }
            }
        }

        for (String group : triggerGroups) {
            for (TriggerKey triggerKey : scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(group))) {
                if (triggerKey.getName().contains(tournamentId.toString())) {
                    scheduler.unscheduleJob(triggerKey);
                    log.info("Unscheduled trigger: {}", triggerKey);
                }
            }
        }

        log.info("Cancelled all scheduled jobs for tournament {}", tournamentId);
    }
}

