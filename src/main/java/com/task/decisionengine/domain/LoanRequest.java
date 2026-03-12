package com.task.decisionengine.domain;

import lombok.Builder;

@Builder
public record LoanRequest(
        String personalCode,
        int amount,
        int period
) {
}
