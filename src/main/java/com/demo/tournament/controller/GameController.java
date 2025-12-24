package com.demo.tournament.controller;

import com.demo.tournament.entity.Tournament;
import com.demo.tournament.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@Validated
public class GameController {
    private final TournamentService tournamentService;

    // Get tournament validation rules
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

    // Create a new game
    @PostMapping
    public ResponseEntity<?> createGame(@RequestBody Tournament tournament) {
        log.info("Creating tournament: {}", tournament);

        try {
            Tournament created = tournamentService.createTournament(tournament);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid tournament data: " + e.getMessage());
        }
    }

    // Update game
    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateGame(@PathVariable Long id, @RequestBody Tournament tournament) {
        return ResponseEntity.ok(tournamentService.updateTournament(id, tournament));
    }

    // View single game
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getGame(@PathVariable Long id) {
        Optional<Tournament> game = tournamentService.getTournamentById(id);
        return game.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // View all games
    @GetMapping
    public ResponseEntity<List<Tournament>> getAllGames() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }
}
