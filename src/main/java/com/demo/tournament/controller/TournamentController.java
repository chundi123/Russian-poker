package com.demo.tournament.controller;

import com.demo.tournament.dto.LeaderboardRowDto;
import com.demo.tournament.dto.LobbyTournamentDto;
import com.demo.tournament.dto.RoundResultRequest;
import com.demo.tournament.entity.RoundResult;
import com.demo.tournament.entity.Tournament;
import com.demo.tournament.entity.TournamentPlayer;
import com.demo.tournament.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin
@Slf4j
@Validated
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping
    public ResponseEntity<?> createTournament(@RequestBody Tournament tournament) {
        try {
            Tournament created = tournamentService.createTournament(tournament);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid tournament data: " + e.getMessage());
        }
    }

    @GetMapping("/validation-rules")
    public ResponseEntity<Map<String, Map<String, Object>>> getTournamentValidationRules() {
        Map<String, Map<String, Object>> rules = Map.of(
            "name", Map.of(
                "required", true,
                "minLength", 3,
                "maxLength", 100
            ),
            "startingChips", Map.of(
                "required", true,
                "min", 100,
                "max", 10000
            ),
            "totalRounds", Map.of(
                "required", false,
                "min", 1,
                "max", 20
            ),
            "maxPlayers", Map.of(
                "required", false,
                "min", 2,
                "max", 1000
            )
        );
        return ResponseEntity.ok(rules);
    }

    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        List<Tournament> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Long id) {
        return tournamentService.getTournamentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable Long id, @RequestBody Tournament tournament) {
        try {
            Tournament updated = tournamentService.updateTournament(id, tournament);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/platforms/{platformId}/lobby")
    public ResponseEntity<List<LobbyTournamentDto>> lobby(@PathVariable Long platformId) {
        List<LobbyTournamentDto> lobby = tournamentService.lobbyForPlatform(platformId);
        return ResponseEntity.ok(lobby);
    }

    @PostMapping("/{tournamentId}/join/{playerId}")
    public ResponseEntity<TournamentPlayer> join(@PathVariable Long tournamentId, @PathVariable Long playerId) {
        try {
            TournamentPlayer tp = tournamentService.joinTournament(tournamentId, playerId);
            return ResponseEntity.ok(tp);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{tournamentId}/leaderboard")
    public ResponseEntity<List<LeaderboardRowDto>> leaderboard(@PathVariable Long tournamentId) {
        try {
            List<LeaderboardRowDto> leaderboard = tournamentService.leaderboard(tournamentId);
            return ResponseEntity.ok(leaderboard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/status")
    public ResponseEntity<?> getTournamentStatus(@PathVariable Long id) {
        return tournamentService.getTournamentById(id)
                .map(tournament -> {
                    String statusCode = tournament.getStatus() != null ? tournament.getStatus().getStatusCode() : null;
                    return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
                        put("tournamentId", tournament.getId());
                        put("status", statusCode);
                        put("currentRound", 0); // Not tracked in TMS schema
                    }});
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTournament(@PathVariable Long id) {
        try {
            tournamentService.cancelTournament(id);
            return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
                put("message", "Tournament cancelled successfully");
                put("tournamentId", id);
            }});
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


