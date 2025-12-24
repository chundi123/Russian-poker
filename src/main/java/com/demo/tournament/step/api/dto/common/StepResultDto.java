package com.demo.tournament.step.api.dto.common;

import com.demo.tournament.step.api.enums.StepResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class StepResultDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private StepResultCode stepResultCode;
    private String description;
    private String resultCode;


}
