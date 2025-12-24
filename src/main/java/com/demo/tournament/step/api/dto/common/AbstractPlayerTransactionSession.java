package com.demo.tournament.step.api.dto.common;

import lombok.Data;

@Data
public class AbstractPlayerTransactionSession {

    private static final long serialVersionUID = 1L;
    private String gameAccount;
    private String externalRoundId;
    private String tournamentId;
    private String playerId;

}
