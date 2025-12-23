package com.demo.tournament.controller;

import com.demo.tournament.dto.LeaderboardRowDto;
import com.demo.tournament.dto.LobbyTournamentDto;
import com.demo.tournament.dto.RoundResultRequest;
import com.demo.tournament.entity.RoundResult;
import com.demo.tournament.entity.Tournament;
import com.demo.tournament.entity.TournamentPlayer;
import com.demo.tournament.service.TournamentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament tournament) {
        try {
            Tournament created = tournamentService.createTournament(tournament);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
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

    @GetMapping("/sites/{siteId}/lobby")
    public ResponseEntity<List<LobbyTournamentDto>> lobby(@PathVariable Long siteId) {
        List<LobbyTournamentDto> lobby = tournamentService.lobbyForSite(siteId);
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

    @PostMapping("/round-results")
    public ResponseEntity<RoundResult> recordRoundResult(@RequestBody RoundResultRequest request) {
        try {
            RoundResult result = tournamentService.recordRoundResult(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
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
                        put("currentRound", tournament.getCurrentRound());
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


