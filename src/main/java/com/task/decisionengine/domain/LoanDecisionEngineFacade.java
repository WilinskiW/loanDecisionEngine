package com.task.decisionengine.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoanDecisionEngineFacade {
    private CreditScoreCalculator calculator;

    public LoanDecisionReportDto decideLoanAmount(UserLoanInfoToReviewDto loanInfo) {
        LoanValidator.validateLoanConstraints(loanInfo.loanAmount(), loanInfo.loanPeriod());
        return calculator.calculate(loanInfo);
    }
}
