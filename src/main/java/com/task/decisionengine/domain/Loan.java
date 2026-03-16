package com.task.decisionengine.domain;

import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;

@Getter(AccessLevel.PACKAGE)
class Loan {
    static final BigDecimal MIN_AMOUNT = new BigDecimal("2000");
    static final BigDecimal MAX_AMOUNT = new BigDecimal("10000");
    static final int MIN_PERIOD = 12;
    static final int MAX_PERIOD = 60;

    private final BigDecimal amount;
    private final int period;

    public Loan(BigDecimal amount, int period) {
        validate(amount, period);
        this.amount = amount;
        this.period = period;
    }


    static void validate(BigDecimal amount, int period) {
        if (amount.compareTo(MIN_AMOUNT) < 0 || amount.compareTo(MAX_AMOUNT) > 0) {
            throw new LoanValidationException("Loan amount must be between 2000 and 10000");
        }

        if (period < MIN_PERIOD || period > MAX_PERIOD) {
            throw new LoanValidationException("Loan period must be between 12 and 60");
        }
    }

    static BigDecimal calculateMaxAmount(int creditModifier, int period) {
        return BigDecimal.valueOf((long) creditModifier * period);
    }

    static boolean isLoanAffordable(BigDecimal amount){
        return amount.compareTo(Loan.MIN_AMOUNT) >= 0;
    }

    static BigDecimal clampAmount(BigDecimal amount) {
        return amount.min(MAX_AMOUNT).max(MIN_AMOUNT);
    }
}
