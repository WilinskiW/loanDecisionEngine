package com.task.decisionengine.domain;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record LoanOffer(
        DecisionOutcome outcome,
        BigDecimal amount,
        int period
) {
    static LoanOffer buildNegativeOutcome(int period){
        return new LoanOffer(DecisionOutcome.NEGATIVE, BigDecimal.ZERO, period);
    }

    static LoanOffer buildPositiveOutcome(int amount, int period){
        return LoanOffer.builder()
                .outcome(DecisionOutcome.POSITIVE)
                .amount(new BigDecimal(Math.min(amount, LoanValidator.MAX_LOAN_AMOUNT.intValue())))
                .period(period)
                .build();
    }
}
