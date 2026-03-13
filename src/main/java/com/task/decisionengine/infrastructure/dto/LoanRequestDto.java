package com.task.decisionengine.infrastructure.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanRequestDto(
        @NotNull(message = "Personal code is required")
        String personalCode,

        @NotNull(message = "Amount is required")
        @Min(value = 2000, message = "Amount must be at least 2000")
        @Max(value = 10000, message = "Amount must be at most 10000")
        BigDecimal amount,

        @NotNull(message = "Period is required")
        @Min(value = 12, message = "Period must be at least 12")
        @Max(value = 60, message = "Period must be at most 60")
        int period
) {
}
