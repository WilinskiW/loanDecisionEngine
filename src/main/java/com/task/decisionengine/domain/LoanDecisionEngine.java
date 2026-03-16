package com.task.decisionengine.domain;


public class LoanDecisionEngine {
    private final LoanOfferCalculator calculator;

    public LoanDecisionEngine(UserCreditRegistry userCreditRegistry) {
        this.calculator = new LoanOfferCalculator(userCreditRegistry);
    }

    public LoanOffer decide(LoanRequest loanInfo) {
        Loan.validate(loanInfo.amount(), loanInfo.period());
        return calculator.calculate(loanInfo);
    }
}
