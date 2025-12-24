package com.demo.tournament.controller;

import java.util.HashMap;
import java.util.Map;

import com.demo.tournament.dto.RoundResultRequest;
import com.demo.tournament.entity.RoundResult;
import com.demo.tournament.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/transactional")
@RequiredArgsConstructor
public class TranscationalController {
    private final TournamentService tournamentService;

    // Place a bet + report result (WIN, LOSE, PUSH)
    @PostMapping("/bet")
    public ResponseEntity<RoundResult> placeBet(@RequestBody RoundResultRequest request) {
        return ResponseEntity.ok(tournamentService.recordRoundResult(request));
    }

    // Adjust a player's chips (simple endpoint for admin adjustment)
    @PostMapping("/adjust")
    public ResponseEntity<String> adjustChips(@RequestBody Map<String, Object> payload) {
        // { "tournamentId": ..., "playerId": ... , "amount": ... }
        // TODO: Implement adjustment logic in service
        return ResponseEntity.ok("Chips adjusted! (not yet implemented)");
    }

    // Cancel a round result (void)
    @PostMapping("/void")
    public ResponseEntity<String> voidBet(@RequestBody Map<String, Object> payload) {
        // { "roundResultId": ... }
        // TODO: Implement void logic in service
        return ResponseEntity.ok("Round voided! (not yet implemented)");
    }

    // Cancel tournament
    @PostMapping("/cancelTournament/{tournamentId}")
    public ResponseEntity<String> cancelTournament(@PathVariable Long tournamentId) {
        try {
            tournamentService.cancelTournament(tournamentId);
            return ResponseEntity.ok("Tournament cancelled.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PAYROLL: Accepts a HashMap with arbitrary structure (dynamic payroll)
    @PostMapping("/payroll")
    public ResponseEntity<Map<String, Object>> payrollDynamic(@RequestBody Map<String, Object> payrollData) {
        // Example: {"employeeId": 123, "type": "BONUS", ... more fields ... }
        // Parentheses used in log message, as requested
        log.info("(Payroll)(Received): {}", payrollData);
        // Do something dynamic with the payroll
        Map<String, Object> response = new HashMap<>();
        response.put("status", "processed");
        response.put("input", payrollData);
        return ResponseEntity.ok(response);
    }
}
