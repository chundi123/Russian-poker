package com.demo.tournament.schedulers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

@AllArgsConstructor
@Slf4j
@DisallowConcurrentExecution
public class TournamentScheduler {

    // Method
    // To trigger the scheduler every 2 seconds with

    @Scheduled(fixedRate = 10000)
    public void scheduleTask()
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss.SSS");

        String strDate = dateFormat.format(new Date());

        log.info(
                "Fixed Delay Scheduler: Task running at - {} "
                        , strDate);
    }
}