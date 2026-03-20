package com.task.decisionengine.domain;


public class LoanDecisionEngine {
    private final LoanOfferCalculator calculator;

    public LoanDecisionEngine(UserCreditRegistry userCreditRegistry) {
        this.calculator = new LoanOfferCalculator(userCreditRegistry);
    }

    public LoanOffer decide(LoanRequest loanInfo) {
        var requestedLoan = new Loan(loanInfo.amount(), loanInfo.period());
        var personalCode = loanInfo.personalCode();
        return calculator.calculate(requestedLoan, personalCode);
    }
}
