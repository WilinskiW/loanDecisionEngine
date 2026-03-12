package com.task.decisionengine.domain;

import lombok.Builder;

@Builder
public record LoanDecisionReportDto(
        DecisionEngineOutcome outcome,
        int amount,
        int period
) {
}
