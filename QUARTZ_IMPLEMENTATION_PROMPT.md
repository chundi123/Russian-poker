# Quartz Scheduler Implementation for Tournament State Management

You are working on a Spring Boot backend for a Russian Poker tournament platform.

## GOAL:
Implement automatic tournament state transitions using Quartz Scheduler.

## BUSINESS FLOW:
Admin creates tournament
→ CREATED (status_code: "CREATED")
→ REGISTERING (status_code: "REGISTERING" - OPEN for players)
→ RUNNING (status_code: "RUNNING")
→ ROUND_1_OPEN
→ ROUND_1_CLOSED
→ ROUND_2_OPEN
→ ...
→ COMPLETED (status_code: "COMPLETED")

**FINAL TOURNAMENT STATES:**
CREATED → REGISTERING → RUNNING → COMPLETED

**ROUND STATES (child flow):**
ROUND_N_OPEN → ROUND_N_CLOSED

## TECH STACK:
- Spring Boot
- PostgreSQL (schema: `rnd_tns`)
- Quartz Scheduler
- JPA / Hibernate
- Existing entity structure with `TournamentStatus` as entity (not enum)

## CURRENT DATABASE STRUCTURE:

### Tournament Entity (`tns_tournament` table):
- `id` (BIGINT, auto-generated)
- `site_id` (FK to `tns_site`)
- `status_id` (FK to `tns_tournament_status`)
- `tournament_type` (VARCHAR: "PVD" or "PVP")
- `name` (VARCHAR)
- `starting_chips` (INTEGER)
- `total_rounds` (INTEGER)
- `max_players` (INTEGER)
- `created_at` (TIMESTAMP)
- `last_updated` (TIMESTAMP)

**NEW FIELDS TO ADD:**
- `registration_start_time` (TIMESTAMP)
- `registration_end_time` (TIMESTAMP)
- `tournament_start_time` (TIMESTAMP)
- `current_round` (INTEGER, nullable)

### TournamentStatus Entity (`tns_tournament_status` table):
- `status_id` (SMALLINT, PK)
- `status_code` (VARCHAR, unique) - Values: "CREATED", "REGISTERING", "RUNNING", "COMPLETED", "CANCELLED"
- `status_name` (VARCHAR)
- `description` (VARCHAR)
- `sort_order` (SMALLINT)
- `created_at` (TIMESTAMP)
- `last_updated` (TIMESTAMP)

### TournamentRound Entity (`tns_tournament_round` table):
- `round_id` (BIGINT, auto-generated)
- `tournament_id` (FK to `tns_tournament`)
- `round_number` (INTEGER)
- `status` (VARCHAR: "OPEN", "CLOSED")
- `created_at` (TIMESTAMP)
- `last_updated` (TIMESTAMP)

**NEW FIELDS TO ADD:**
- `round_start_time` (TIMESTAMP)
- `round_end_time` (TIMESTAMP)

## EXISTING CODE STRUCTURE:

### Current API Endpoints:
- `POST /api/tournaments` - Create tournament (TournamentController)
- `GET /api/tournaments` - List all tournaments
- `GET /api/tournaments/{id}` - Get tournament by ID
- `PUT /api/tournaments/{id}` - Update tournament
- `GET /api/tournaments/{tournamentId}/leaderboard` - Get leaderboard

### Current Service:
- `TournamentService` - Contains business logic
- Uses `TournamentStatusRepository.findByStatusCode(String)` to get status entities
- Defaults to "CREATED" status when creating tournament

### Current Repository Pattern:
- `TournamentRepository extends JpaRepository<Tournament, Long>`
- `TournamentStatusRepository extends JpaRepository<TournamentStatus, Short>`
- `TournamentRoundRepository extends JpaRepository<TournamentRound, Long>`

## TASKS TO IMPLEMENT:

### 1️⃣ Update Tournament Entity
Add new fields to `Tournament.java`:
- `registrationStartTime` (Instant)
- `registrationEndTime` (Instant)
- `tournamentStartTime` (Instant)
- `currentRound` (Integer, nullable)

### 2️⃣ Update TournamentRound Entity
Add new fields to `TournamentRound.java`:
- `roundStartTime` (Instant)
- `roundEndTime` (Instant)

### 3️⃣ Quartz Jobs
Create Quartz Jobs in `com.demo.tournament.job` package:
- `TournamentRegistrationOpenJob` - Transitions CREATED → REGISTERING
- `TournamentStartJob` - Transitions REGISTERING → RUNNING
- `RoundOpenJob` - Opens a specific round (status: "OPEN")
- `RoundCloseJob` - Closes a specific round (status: "CLOSED")
- `TournamentCompleteJob` - Transitions RUNNING → COMPLETED

Each job must:
- Load tournament by ID from JobDataMap
- Validate current state using `TournamentStatus.statusCode`
- Transition to next valid state only
- Use `TournamentStatusRepository.findByStatusCode()` to get status entity
- Persist changes safely with `@Transactional`
- Handle `IllegalArgumentException` for invalid transitions

### 4️⃣ Scheduling Logic
When Admin creates a tournament via `TournamentService.createTournament()`:
- Save tournament with CREATED status (existing logic)
- Schedule:
  - `TournamentRegistrationOpenJob` at `registrationStartTime`
  - `TournamentStartJob` at `tournamentStartTime`

When tournament moves to RUNNING:
- Auto-create rounds based on `totalRounds`
- Schedule:
  - `RoundOpenJob` for ROUND_1 immediately or at configured time
  - `RoundCloseJob` for ROUND_1 at `roundEndTime`
  - Repeat for all rounds sequentially

After last round CLOSED:
- Transition tournament to COMPLETED via `TournamentCompleteJob`

### 5️⃣ Quartz Configuration
- Use JDBC JobStore (persist in PostgreSQL)
- Configure in `application.properties`:
  ```
  spring.quartz.job-store-type=jdbc
  spring.quartz.jdbc.initialize-schema=always
  ```
- Use clustered mode safe configuration
- Use `@DisallowConcurrentExecution` on jobs where required
- Create `QuartzConfig.java` for SchedulerFactoryBean configuration

### 6️⃣ Service Layer
Create `TournamentSchedulerService`:
- `scheduleRegistrationOpen(Long tournamentId, Instant registrationStartTime)`
- `scheduleTournamentStart(Long tournamentId, Instant tournamentStartTime)`
- `scheduleRounds(Long tournamentId)` - Creates and schedules all rounds
- `scheduleTournamentCompletion(Long tournamentId, Instant completionTime)`
- `cancelScheduledJobs(Long tournamentId)` - Cancel all jobs for a tournament

### 7️⃣ Update TournamentService
Modify `createTournament()` method:
- After saving tournament, call `TournamentSchedulerService.scheduleRegistrationOpen()`
- Call `TournamentSchedulerService.scheduleTournamentStart()`
- Ensure time validations (registrationStartTime < registrationEndTime < tournamentStartTime)

### 8️⃣ API Endpoints (Update Existing)
Keep existing endpoints in `TournamentController`:
- `POST /api/tournaments` - Already exists, will auto-schedule jobs
- `GET /api/tournaments/{id}` - Already exists
- `GET /api/tournaments` - Already exists

Add new admin endpoints:
- `GET /api/tournaments/{id}/status` - Get current status
- `POST /api/tournaments/{id}/cancel` - Cancel tournament and all scheduled jobs

### 9️⃣ Validation Rules
- Prevent invalid state transitions:
  - CREATED → REGISTERING (only at registrationStartTime)
  - REGISTERING → RUNNING (only at tournamentStartTime)
  - RUNNING → COMPLETED (only after last round closed)
- Do not allow REGISTERING if already RUNNING
- Do not allow RUNNING if registration not completed
- Ensure idempotent job execution (safe retry)
- Validate time sequences: registrationStartTime < registrationEndTime < tournamentStartTime

### 🔟 Error Handling
Create `GlobalExceptionHandler` with `@ControllerAdvice`:
- `TournamentNotFoundException` - 404
- `InvalidStateTransitionException` - 400
- `QuartzSchedulerException` - 500
- Log all exceptions with context

### 1️⃣1️⃣ Logging & Monitoring
- Log every state transition with tournament ID and status codes
- Log Quartz job execution start/end with timing
- Handle job failures gracefully with retry logic
- Use SLF4J logger in all jobs and services

### 1️⃣2️⃣ Repository Updates
Add query methods to `TournamentRoundRepository`:
- `List<TournamentRound> findByTournamentOrderByRoundNumberAsc(Tournament tournament)` - Already exists
- `Optional<TournamentRound> findFirstByTournamentOrderByRoundNumberDesc(Tournament tournament)` - Already exists
- Add method to find all OPEN rounds for a tournament

## DELIVERABLES:
- Updated `Tournament.java` entity with time fields
- Updated `TournamentRound.java` entity with time fields
- Quartz Job classes (5 jobs)
- `TournamentSchedulerService` class
- `QuartzConfig.java` configuration
- Updated `TournamentService.createTournament()` method
- New admin endpoints in `TournamentController`
- `GlobalExceptionHandler` for error handling
- Updated `application.properties` with Quartz config
- Clean, readable, production-quality code

## IMPORTANT:
- Use existing code patterns (service layer, repository pattern)
- Use `TournamentStatus` entity (not enum) - work with `statusCode` strings
- Use `Long` for IDs (not UUID)
- Use `Instant` for timestamps (matching existing pattern)
- Use `@Transactional` on service methods
- Follow existing package structure: `com.demo.tournament.*`
- Code must be clean, maintainable, and scalable
- Do NOT change database queries - only add new fields and methods
- Ensure backward compatibility with existing APIs

