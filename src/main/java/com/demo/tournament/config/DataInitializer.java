package com.demo.tournament.config;

import com.demo.tournament.entity.TournamentStatus;
import com.demo.tournament.repository.TournamentStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final TournamentStatusRepository tournamentStatusRepository;

    public DataInitializer(TournamentStatusRepository tournamentStatusRepository) {
        this.tournamentStatusRepository = tournamentStatusRepository;
    }

    @Override
    public void run(String... args) {
        logger.info("Checking tournament_status table initialization...");
        
        long count = tournamentStatusRepository.count();
        logger.info("Current tournament_status records: {}", count);
        
        if (count == 0) {
            logger.info("Initializing tournament_status table...");
            initializeTournamentStatuses();
            logger.info("Tournament status initialization completed!");
        } else {
            logger.info("Tournament status table already initialized with {} records", count);
        }
    }

    private void initializeTournamentStatuses() {
        TournamentStatus created = new TournamentStatus();
        created.setStatusId((short) 1);
        created.setStatusCode("CREATED");
        created.setStatusName("Created");
        created.setDescription("Tournament created, registrations open");
        created.setSortOrder((short) 1);
        tournamentStatusRepository.save(created);

        TournamentStatus registering = new TournamentStatus();
        registering.setStatusId((short) 2);
        registering.setStatusCode("REGISTERING");
        registering.setStatusName("Registering");
        registering.setDescription("Players can join");
        registering.setSortOrder((short) 2);
        tournamentStatusRepository.save(registering);

        TournamentStatus ready = new TournamentStatus();
        ready.setStatusId((short) 3);
        ready.setStatusCode("READY");
        ready.setStatusName("Ready");
        ready.setDescription("Enough players, waiting to start");
        ready.setSortOrder((short) 3);
        tournamentStatusRepository.save(ready);

        TournamentStatus running = new TournamentStatus();
        running.setStatusId((short) 4);
        running.setStatusCode("RUNNING");
        running.setStatusName("Running");
        running.setDescription("Tournament in progress");
        running.setSortOrder((short) 4);
        tournamentStatusRepository.save(running);

        TournamentStatus paused = new TournamentStatus();
        paused.setStatusId((short) 5);
        paused.setStatusCode("PAUSED");
        paused.setStatusName("Paused");
        paused.setDescription("Temporarily paused");
        paused.setSortOrder((short) 5);
        tournamentStatusRepository.save(paused);

        TournamentStatus completed = new TournamentStatus();
        completed.setStatusId((short) 6);
        completed.setStatusCode("COMPLETED");
        completed.setStatusName("Completed");
        completed.setDescription("Tournament finished");
        completed.setSortOrder((short) 6);
        tournamentStatusRepository.save(completed);

        TournamentStatus cancelled = new TournamentStatus();
        cancelled.setStatusId((short) 7);
        cancelled.setStatusCode("CANCELLED");
        cancelled.setStatusName("Cancelled");
        cancelled.setDescription("Tournament cancelled");
        cancelled.setSortOrder((short) 7);
        tournamentStatusRepository.save(cancelled);
    }
}

