package com.demo.tournament.controller;

import com.demo.tournament.entity.Tournament;
import com.demo.tournament.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {
    private final TournamentService tournamentService;

    // Create a new game
    @PostMapping
    public ResponseEntity<Tournament> createGame(@RequestBody Tournament tournament) {
        return ResponseEntity.ok(tournamentService.createTournament(tournament));
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
