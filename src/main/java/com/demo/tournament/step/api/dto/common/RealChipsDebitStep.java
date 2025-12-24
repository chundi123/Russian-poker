package com.demo.tournament.step.api.dto.common;

import com.demo.tournament.interfaces.AbstractTransactionSystemStep;
import org.springframework.stereotype.Component;

@Component(RealChipsDebitStep.PLUGIN_ID)
public class RealChipsDebitStep extends AbstractTransactionSystemStep {

    public static final String  PLUGIN_ID = "REAL_CHIPS_DEBIT";

    @Override
    public StepResultDto executeStep(AbstractPlayerTransactionSession session, String stepParameters) {
        return null;
    }

    @Override
    public StepResultDto isToSkip(AbstractPlayerTransactionSession session, String stepParameters) {
        return null;
    }
}
