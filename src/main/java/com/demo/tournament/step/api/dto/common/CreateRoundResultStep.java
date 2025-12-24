package com.demo.tournament.step.api.dto.common;

import com.demo.tournament.interfaces.AbstractTransactionSystemStep;
import org.springframework.stereotype.Component;

@Component(CreateRoundResultStep.PLUGIN_ID)
public class CreateRoundResultStep extends AbstractTransactionSystemStep {

    public static final String  PLUGIN_ID = "CREATE_GAME_ROUND_RESULT";

    @Override
    public StepResultDto executeStep(AbstractPlayerTransactionSession session, String stepParameters) {
        return null;
    }

    @Override
    public StepResultDto isToSkip(AbstractPlayerTransactionSession session, String stepParameters) {
        return null;
    }
}
