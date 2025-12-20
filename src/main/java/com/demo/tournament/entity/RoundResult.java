package com.demo.tournament.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(
    name = "tns_round_result",
    uniqueConstraints = @UniqueConstraint(columnNames = {"round_id", "player_id"})
)
public class RoundResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_result_id")
    private Long roundResultId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "round_id", nullable = false)
    private TournamentRound round;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Account player;

    @Column(name = "bet_chips", nullable = false)
    private Integer betChips;

    @Column(name = "chips_delta", nullable = false)
    private Integer chipsDelta;

    @Column(name = "chips_after", nullable = false)
    private Integer chipsAfter;

    @Column(name = "result", length = 10)
    private String result;

    @CreationTimestamp
    @Column(name = "recorded_at", nullable = false, updatable = false)
    private Instant recordedAt;

    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    public Long getRoundResultId() {
        return roundResultId;
    }

    public void setRoundResultId(Long roundResultId) {
        this.roundResultId = roundResultId;
    }

    public TournamentRound getRound() {
        return round;
    }

    public void setRound(TournamentRound round) {
        this.round = round;
    }

    public Account getPlayer() {
        return player;
    }

    public void setPlayer(Account player) {
        this.player = player;
    }

    public Integer getBetChips() {
        return betChips;
    }

    public void setBetChips(Integer betChips) {
        this.betChips = betChips;
    }

    public Integer getChipsDelta() {
        return chipsDelta;
    }

    public void setChipsDelta(Integer chipsDelta) {
        this.chipsDelta = chipsDelta;
    }

    public Integer getChipsAfter() {
        return chipsAfter;
    }

    public void setChipsAfter(Integer chipsAfter) {
        this.chipsAfter = chipsAfter;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
