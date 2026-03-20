package com.task.decisionengine.domain;

import java.math.BigDecimal;

class Loan {
    private final BigDecimal amount;
    private final int period;

    static final BigDecimal MIN_AMOUNT = new BigDecimal("2000");
    static final BigDecimal MAX_AMOUNT = new BigDecimal("10000");
    static final int MIN_PERIOD = 12;
    static final int MAX_PERIOD = 60;


    public Loan(final BigDecimal amount, final int period) {
        this.amount = amount;
        this.period = period;
        validate();
    }

    private void validate(){
        validateAmount();
        validatePeriod();
    }

    private void validateAmount() {
        if (amount.compareTo(MIN_AMOUNT) < 0) {
            throw new InvalidLoanException("Loan amount %s is below minimum %s".formatted(amount, MIN_AMOUNT));
        }

        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new InvalidLoanException("Loan amount %s exceeds maximum %s".formatted(amount, MAX_AMOUNT));
        }
    }

    private void validatePeriod() {
        if (period < MIN_PERIOD) {
            throw new InvalidLoanException("Loan period %s months is below minimum %s".formatted(period, MIN_PERIOD));
        }

        if (period > MAX_PERIOD){
            throw new InvalidLoanException("Loan period %s months exceeds maximum %s".formatted(period, MAX_PERIOD));
        }
    }

}
