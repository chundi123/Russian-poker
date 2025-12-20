package com.demo.tournament.dto;

public class LobbyTournamentDto {

    private Long tournamentId;
    private String name;
    private Integer startingChips;
    private Integer totalRounds;
    private Integer maxPlayers;
    private String status;
    private long joinedPlayers;
    private String tournamentType;

    public LobbyTournamentDto() {
    }

    public LobbyTournamentDto(Long tournamentId, String name, Integer startingChips, Integer totalRounds,
                              Integer maxPlayers, String status, long joinedPlayers) {
        this.tournamentId = tournamentId;
        this.name = name;
        this.startingChips = startingChips;
        this.totalRounds = totalRounds;
        this.maxPlayers = maxPlayers;
        this.status = status;
        this.joinedPlayers = joinedPlayers;
    }

    public LobbyTournamentDto(Long tournamentId, String name, Integer startingChips, Integer totalRounds,
                              Integer maxPlayers, String status, long joinedPlayers, String tournamentType) {
        this.tournamentId = tournamentId;
        this.name = name;
        this.startingChips = startingChips;
        this.totalRounds = totalRounds;
        this.maxPlayers = maxPlayers;
        this.status = status;
        this.joinedPlayers = joinedPlayers;
        this.tournamentType = tournamentType;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStartingChips() {
        return startingChips;
    }

    public void setStartingChips(Integer startingChips) {
        this.startingChips = startingChips;
    }

    public Integer getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getJoinedPlayers() {
        return joinedPlayers;
    }

    public void setJoinedPlayers(long joinedPlayers) {
        this.joinedPlayers = joinedPlayers;
    }

    public String getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(String tournamentType) {
        this.tournamentType = tournamentType;
    }
}


