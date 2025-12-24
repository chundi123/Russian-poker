package com.demo.tournament.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(
    name = "tms_tournament_player",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "player_id"})
)
public class TournamentPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Account player;

    @Column(name = "chips_start", nullable = false)
    private Integer chipsStart;

    @Column(name = "chips_current", nullable = false)
    private Integer chipsCurrent;

    @Column(name = "total_wins")
    private Integer totalWins = 0;

    @Column(name = "total_losses")
    private Integer totalLosses = 0;

    @Column(name = "total_pushes")
    private Integer totalPushes = 0;

    @Column(name = "final_rank")
    private Integer finalRank;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @Column(name = "chips_reserved")
    private Integer chipsReserved;

    @Column(name = "chips_available")
    private Integer chipsAvailable;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Account getPlayer() {
        return player;
    }

    public void setPlayer(Account player) {
        this.player = player;
    }

    public Integer getChipsStart() {
        return chipsStart;
    }

    public void setChipsStart(Integer chipsStart) {
        this.chipsStart = chipsStart;
    }

    public Integer getChipsCurrent() {
        return chipsCurrent;
    }

    public void setChipsCurrent(Integer chipsCurrent) {
        this.chipsCurrent = chipsCurrent;
    }

    public Integer getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(Integer totalWins) {
        this.totalWins = totalWins;
    }

    public Integer getTotalLosses() {
        return totalLosses;
    }

    public void setTotalLosses(Integer totalLosses) {
        this.totalLosses = totalLosses;
    }

    public Integer getTotalPushes() {
        return totalPushes;
    }

    public void setTotalPushes(Integer totalPushes) {
        this.totalPushes = totalPushes;
    }

    public Integer getFinalRank() {
        return finalRank;
    }

    public void setFinalRank(Integer finalRank) {
        this.finalRank = finalRank;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Integer getChipsReserved() {
        return chipsReserved;
    }

    public void setChipsReserved(Integer chipsReserved) {
        this.chipsReserved = chipsReserved;
    }

    public Integer getChipsAvailable() {
        return chipsAvailable;
    }

    public void setChipsAvailable(Integer chipsAvailable) {
        this.chipsAvailable = chipsAvailable;
    }
}