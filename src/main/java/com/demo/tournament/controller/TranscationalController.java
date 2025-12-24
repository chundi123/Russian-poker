package com.demo.tournament.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.demo.tournament.dto.PlayerTransactionHistoryDto;
import com.demo.tournament.dto.RoundResultRequest;
import com.demo.tournament.entity.RoundResult;
import com.demo.tournament.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        // Process dynamic payroll data
        Map<String, Object> response = new HashMap<>();
        response.put("status", "processed");
        response.put("input", payrollData);
        return ResponseEntity.ok(response);
    }

    // OWNER VIEW: Get complete player transaction history (all details)
    @GetMapping("/player/{playerId}/transactions")
    public ResponseEntity<List<PlayerTransactionHistoryDto>> getPlayerTransactionHistory(@PathVariable Long playerId) {
        try {
            List<PlayerTransactionHistoryDto> history = tournamentService.getPlayerTransactionHistory(playerId);
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // OWNER VIEW: Get player transaction summary with statistics
    @GetMapping("/player/{playerId}/transaction-summary")
    public ResponseEntity<Map<String, Object>> getPlayerTransactionSummary(@PathVariable Long playerId) {
        try {
            Map<String, Object> summary = tournamentService.getPlayerTransactionSummary(playerId);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // OWNER VIEW: Get all transactions across all players (admin overview)
    @GetMapping("/admin/all-transactions")
    public ResponseEntity<List<PlayerTransactionHistoryDto>> getAllTransactions() {
        // This would require a new service method to get all transactions
        // For now, return empty list as this needs admin authorization logic
        List<PlayerTransactionHistoryDto> allTransactions = new java.util.ArrayList<>();
        return ResponseEntity.ok(allTransactions);
    }
}
