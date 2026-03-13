package com.task.decisionengine.infrastructure.dto;

import java.math.BigDecimal;

public record LoanRequestDto(
        String personalCode,
        BigDecimal amount,
        int period
) {
}
