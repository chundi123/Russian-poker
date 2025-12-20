package com.demo.tournament.dto;

public class RoundResultRequest {

    private Long tournamentId;
    private int roundNumber;
    private Long playerId;
    private int betChips;
    /**
     * WIN, LOSE, PUSH
     */
    private String result;

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public int getBetChips() {
        return betChips;
    }

    public void setBetChips(int betChips) {
        this.betChips = betChips;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}


