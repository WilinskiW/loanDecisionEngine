package com.task.decisionengine.domain;

import lombok.Builder;

@Builder
public record UserLoanInfoToReviewDto(
        String personalCode,
        double amount,
        int period
) {
}
