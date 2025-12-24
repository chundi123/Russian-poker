package com.demo.tournament.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "tms_tournament")
@Getter
@Setter
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id")
    private Platform platform;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private TournamentStatus status;

    @Column(name = "tournament_type", length = 10)
    private String tournamentType = "PVD";

    @NotBlank(message = "Tournament name is required")
    @Size(min = 3, max = 100, message = "Tournament name must be between 3 and 100 characters")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull(message = "Starting chips is required")
    @Min(value = 100, message = "Starting chips must be at least 100")
    @Max(value = 10000, message = "Starting chips cannot exceed 10,000")
    @Column(name = "starting_chips", nullable = false)
    private Integer startingChips;

    @Min(value = 1, message = "Total rounds must be at least 1")
    @Max(value = 20, message = "Total rounds cannot exceed 20")
    @Column(name = "total_rounds")
    private Integer totalRounds;

    @Min(value = 2, message = "Maximum players must be at least 2")
    @Max(value = 1000, message = "Maximum players cannot exceed 1,000")
    @Column(name = "max_players")
    private Integer maxPlayers;

    @Column(name = "registration_start_time")
    private Instant registrationStartTime;

    @Column(name = "registration_end_time")
    private Instant registrationEndTime;

    @Column(name = "tournament_start_time")
    private Instant tournamentStartTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public String getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(String tournamentType) {
        this.tournamentType = tournamentType;
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

    public Instant getRegistrationStartTime() {
        return registrationStartTime;
    }

    public void setRegistrationStartTime(Instant registrationStartTime) {
        this.registrationStartTime = registrationStartTime;
    }

    public Instant getRegistrationEndTime() {
        return registrationEndTime;
    }

    public void setRegistrationEndTime(Instant registrationEndTime) {
        this.registrationEndTime = registrationEndTime;
    }

    public Instant getTournamentStartTime() {
        return tournamentStartTime;
    }

    public void setTournamentStartTime(Instant tournamentStartTime) {
        this.tournamentStartTime = tournamentStartTime;
    }

}
