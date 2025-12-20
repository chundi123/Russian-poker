package com.demo.tournament.dto;

public class LeaderboardRowDto {

    private String username;
    private int chipsCurrent;
    private int totalWins;
    private int totalLosses;

    public LeaderboardRowDto() {
    }

    public LeaderboardRowDto(String username, int chipsCurrent, int totalWins, int totalLosses) {
        this.username = username;
        this.chipsCurrent = chipsCurrent;
        this.totalWins = totalWins;
        this.totalLosses = totalLosses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getChipsCurrent() {
        return chipsCurrent;
    }

    public void setChipsCurrent(int chipsCurrent) {
        this.chipsCurrent = chipsCurrent;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public void setTotalLosses(int totalLosses) {
        this.totalLosses = totalLosses;
    }
}


