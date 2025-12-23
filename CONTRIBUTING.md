# Contributing to Russian Poker Tournament System

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/chundi123/Russian-poker.git
cd Russian-poker
```

### 2. Setup Development Environment
- Java 17 or higher
- Maven (included with Spring Boot)
- PostgreSQL database

### 3. Configure Database
Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://YOUR_HOST:5432/YOUR_DATABASE
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

Or use your IDE to run `TournamentApplication.java`

## Making Changes

### Workflow
1. **Pull latest changes**
   ```bash
   git pull origin main
   ```

2. **Create a new branch for your feature**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**

4. **Commit your changes**
   ```bash
   git add .
   git commit -m "Description of your changes"
   ```

5. **Push your branch**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request on GitHub** (recommended for code review)

   OR

   **Push directly to main** (if you have write access)
   ```bash
   git checkout main
   git merge feature/your-feature-name
   git push origin main
   ```

## Project Structure
```
src/main/java/com/demo/tournament/
├── controller/     # REST API endpoints
├── service/        # Business logic
├── repository/     # Data access layer
├── entity/         # JPA entities
└── dto/            # Data Transfer Objects

src/main/resources/
└── application.properties  # Configuration
```

## API Endpoints

### Sites
- `POST /api/sites` - Create site
- `GET /api/sites` - List all sites
- `GET /api/sites/{id}` - Get site by ID

### Accounts
- `POST /api/accounts` - Create account
- `GET /api/accounts` - List all accounts
- `GET /api/accounts/site/{siteId}` - Get accounts by site

### Tournaments
- `POST /api/tournaments` - Create tournament
- `GET /api/tournaments` - List all tournaments
- `GET /api/tournaments/sites/{siteId}/lobby` - Get tournament lobby
- `POST /api/tournaments/{tournamentId}/join/{playerId}` - Join tournament
- `GET /api/tournaments/{tournamentId}/leaderboard` - Get leaderboard
- `POST /api/tournaments/round-results` - Record round result

## Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Keep methods focused and small

## Database Schema
The database schema includes:
- `tns_site` - Sites
- `tns_account` - Player accounts
- `tns_tournament_status` - Tournament status master
- `tns_tournament` - Tournaments
- `tns_tournament_player` - Tournament players (chips)
- `tns_tournament_round` - Tournament rounds
- `tns_round_result` - Round results

## Need Help?
Contact the repository owner or create an issue on GitHub.




