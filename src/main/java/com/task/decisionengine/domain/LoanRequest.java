package com.task.decisionengine.domain;

import lombok.Builder;

@Builder
public record LoanRequest(
        String personalCode,
        double amount,
        int period
) {
}
