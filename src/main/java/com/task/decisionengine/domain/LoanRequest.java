package com.task.decisionengine.domain;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record LoanRequest(
        String personalCode,
        BigDecimal amount,
        int period
) {
}
