package com.demo.tournament.interfaces;

import com.demo.tournament.step.api.dto.common.AbstractPlayerTransactionSession;
import com.demo.tournament.step.api.dto.common.StepResultDto;

public interface PlayerTransactionSystemStep {

    StepResultDto executeStep(AbstractPlayerTransactionSession session, String stepParameters);

    StepResultDto isToSkip(AbstractPlayerTransactionSession session, String stepParameters);
}
