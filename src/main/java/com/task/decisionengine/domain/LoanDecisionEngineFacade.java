package com.task.decisionengine.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoanDecisionEngineFacade {
    private CreditScoreCalculator calculator;

    public LoanDecisionReportDto decideLoanAmount(UserLoanInfoToReviewDto loanInfo) {
        return calculator.calculate(loanInfo);
    }
}
