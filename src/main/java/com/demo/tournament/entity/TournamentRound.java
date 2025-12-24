package com.demo.tournament.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(
    name = "tms_tournament_round",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "round_number"})
)
public class TournamentRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_id")
    private Long roundId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "round_start_time")
    private Instant roundStartTime;

    @Column(name = "round_end_time")
    private Instant roundEndTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @Column(name = "external_round_id")
    private String externalRoundId;

    @Column(name = "win_chips")
    private Integer winChips;

    @Column(name = "bet_chips")
    private Integer betChips;

    public Long getRoundId() {
        return roundId;
    }

    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Instant getRoundStartTime() {
        return roundStartTime;
    }

    public void setRoundStartTime(Instant roundStartTime) {
        this.roundStartTime = roundStartTime;
    }

    public Instant getRoundEndTime() {
        return roundEndTime;
    }

    public void setRoundEndTime(Instant roundEndTime) {
        this.roundEndTime = roundEndTime;
    }

    public String getExternalRoundId() {
        return externalRoundId;
    }

    public void setExternalRoundId(String externalRoundId) {
        this.externalRoundId = externalRoundId;
    }

    public Integer getWinChips() {
        return winChips;
    }

    public void setWinChips(Integer winChips) {
        this.winChips = winChips;
    }

    public Integer getBetChips() {
        return betChips;
    }

    public void setBetChips(Integer betChips) {
        this.betChips = betChips;
    }
}
