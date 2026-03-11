package com.task.decisionengine.domain;

class CreditScoreCalculator {
    private final UserProfileRegistry userProfileRegistry = new MockUserRegistry();

    LoanDecisionReportDto calculate(UserLoanInfoToReviewDto loanInfo){
        double creditModifier = userProfileRegistry.findUserCreditModifierByPersonalCode(loanInfo.personalCode());
        double creditScore = (creditModifier / loanInfo.loanAmount()) * loanInfo.loanPeriod();

        if(creditScore < 1){
            return LoanDecisionReportDto.builder()
                    .outcome(DecisionEngineOutcome.NEGATIVE)
                    .amount(0)
                    .build();
        }

        return LoanDecisionReportDto.builder()
                .outcome(DecisionEngineOutcome.POSITIVE)
                .amount(creditScore)
                .build();
    }
}
