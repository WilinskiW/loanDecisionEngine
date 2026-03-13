package com.task.decisionengine.domain;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record LoanOffer(
        DecisionOutcome outcome,
        BigDecimal amount,
        int period
) {
}
