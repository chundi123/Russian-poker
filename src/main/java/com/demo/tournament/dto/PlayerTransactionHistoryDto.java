package com.demo.tournament.dto;

import java.time.Instant;

public class PlayerTransactionHistoryDto {
    private Long transactionId;
    private Long tournamentId;
    private String tournamentName;
    private int roundNumber;
    private int betChips;
    private String result;
    private int chipsDelta;
    private int chipsAfter;
    private Instant transactionTime;

    public PlayerTransactionHistoryDto() {}

    public PlayerTransactionHistoryDto(Long transactionId, Long tournamentId, String tournamentName,
                                     int roundNumber, int betChips, String result, int chipsDelta,
                                     int chipsAfter, Instant transactionTime) {
        this.transactionId = transactionId;
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.roundNumber = roundNumber;
        this.betChips = betChips;
        this.result = result;
        this.chipsDelta = chipsDelta;
        this.chipsAfter = chipsAfter;
        this.transactionTime = transactionTime;
    }

    // Getters and setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }

    public int getBetChips() { return betChips; }
    public void setBetChips(int betChips) { this.betChips = betChips; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public int getChipsDelta() { return chipsDelta; }
    public void setChipsDelta(int chipsDelta) { this.chipsDelta = chipsDelta; }

    public int getChipsAfter() { return chipsAfter; }
    public void setChipsAfter(int chipsAfter) { this.chipsAfter = chipsAfter; }

    public Instant getTransactionTime() { return transactionTime; }
    public void setTransactionTime(Instant transactionTime) { this.transactionTime = transactionTime; }
}
