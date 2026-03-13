package com.task.decisionengine.infrastructure.dto;

import com.task.decisionengine.domain.DecisionOutcome;

import java.math.BigDecimal;

public record LoanOfferResponseDto(
        DecisionOutcome outcome,
        BigDecimal amount
) {
}
