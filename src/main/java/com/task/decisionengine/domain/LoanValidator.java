package com.task.decisionengine.domain;

public class LoanValidator {
    private static final int MIN_LOAN_AMOUNT = 2000;
    private static final int MAX_LOAN_AMOUNT = 10_000;
    private static final int MIN_LOAN_PERIOD = 12;
    private static final int MAX_LOAN_PERIOD = 60;


    static void validateLoanConstraints(double loanAmount, int loanPeriod) {
        if (loanAmount < MIN_LOAN_AMOUNT || loanAmount > MAX_LOAN_AMOUNT) {
            throw new LoanValidationException("Loan amount must be between 2000 and 10000");
        }

        if (loanPeriod < MIN_LOAN_PERIOD || loanPeriod > MAX_LOAN_PERIOD) {
            throw new LoanValidationException("Loan period must be between 12 and 60");
        }
    }
}
