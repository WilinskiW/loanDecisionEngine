package com.task.decisionengine.domain;

import java.math.BigDecimal;

class LoanValidator {
    static final BigDecimal MIN_LOAN_AMOUNT = new BigDecimal("2000");
    static final BigDecimal MAX_LOAN_AMOUNT = new BigDecimal("10000");
    static final int MIN_LOAN_PERIOD = 12;
    static final int MAX_LOAN_PERIOD = 60;


    static void validate(BigDecimal amount, int period) {
        if (amount.compareTo(MIN_LOAN_AMOUNT) < 0 || amount.compareTo(MAX_LOAN_AMOUNT) > 0) {
            throw new LoanValidationException("Loan amount must be between 2000 and 10000");
        }

        if (period < MIN_LOAN_PERIOD || period > MAX_LOAN_PERIOD) {
            throw new LoanValidationException("Loan period must be between 12 and 60");
        }
    }
}
