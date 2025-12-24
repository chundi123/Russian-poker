package com.demo.tournament.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "tms_player_purchase_tnxs")
public class PlayerPurchaseTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount_type")
    private String amountType;

    @Column(name = "game_account")
    private String gameAccount;

    @Column(name = "creationdate")
    private Instant creationDate;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    private TournamentRound round;

    @Column(name = "external_round_id")
    private String externalRoundId;

    @Column(name = "external_transaction_id")
    private String externalTransactionId;

    @Column(name = "result_code")
    private String resultCode;

    @Column(name = "result_description")
    private String resultDescription;

    @Column(name = "last_step_excecuted")
    private String lastStepExecuted;

    @Column(name = "process_status")
    private String processStatus;

    @Column(name = "status")
    private String status;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAmountType() {
        return amountType;
    }

    public void setAmountType(String amountType) {
        this.amountType = amountType;
    }

    public String getGameAccount() {
        return gameAccount;
    }

    public void setGameAccount(String gameAccount) {
        this.gameAccount = gameAccount;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public TournamentRound getRound() {
        return round;
    }

    public void setRound(TournamentRound round) {
        this.round = round;
    }

    public String getExternalRoundId() {
        return externalRoundId;
    }

    public void setExternalRoundId(String externalRoundId) {
        this.externalRoundId = externalRoundId;
    }

    public String getExternalTransactionId() {
        return externalTransactionId;
    }

    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getLastStepExecuted() {
        return lastStepExecuted;
    }

    public void setLastStepExecuted(String lastStepExecuted) {
        this.lastStepExecuted = lastStepExecuted;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
