package com.task.decisionengine.domain;

import lombok.Builder;

@Builder
public record UserLoanInfoToReviewDto(
        String personalCode,
        double loanAmount,
        int loanPeriod
) {
}
