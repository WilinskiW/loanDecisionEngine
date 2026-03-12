package com.task.decisionengine.domain;

import lombok.Builder;

@Builder
public record LoanOffer(
        DecisionOutcome outcome,
        int amount,
        int period
) {
}
