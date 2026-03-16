package com.task.decisionengine.domain;

import lombok.Builder;

import java.math.BigDecimal;

import static com.task.decisionengine.domain.Loan.clampAmount;

@Builder
public record LoanOffer(
        DecisionOutcome outcome,
        BigDecimal amount,
        int period
) {
    static LoanOffer buildNegativeOutcome(int period){
        return new LoanOffer(DecisionOutcome.NEGATIVE, BigDecimal.ZERO, period);
    }

    static LoanOffer buildPositiveOutcome(BigDecimal amount, int period){
        return LoanOffer.builder()
                .outcome(DecisionOutcome.POSITIVE)
                .amount(clampAmount(amount))
                .period(period)
                .build();
    }
}
