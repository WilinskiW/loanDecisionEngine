package com.task.decisionengine.domain;

class CreditScoreCalculator {
    private final UserProfileRegistry userProfileRegistry;

    CreditScoreCalculator() {
        this.userProfileRegistry = new MockUserRegistry();
    }

    LoanDecisionReportDto calculate(UserLoanInfoToReviewDto loanInfo) {
        int creditModifier = userProfileRegistry.findUserCreditModifierByPersonalCode(loanInfo.personalCode());

        if (creditModifier <= 0) {
            return LoanDecisionReportDto.builder()
                    .outcome(DecisionEngineOutcome.NEGATIVE)
                    .amount(0)
                    .period(loanInfo.period())
                    .build();
        }

        int maxAmount = creditModifier * loanInfo.period();

        if (maxAmount >= LoanValidator.MIN_LOAN_AMOUNT) {
            return LoanDecisionReportDto.builder()
                    .outcome(DecisionEngineOutcome.POSITIVE)
                    .amount(Math.min(maxAmount, LoanValidator.MAX_LOAN_AMOUNT))
                    .period(loanInfo.period())
                    .build();
        }


        int requiredPeriod = LoanValidator.MIN_LOAN_AMOUNT / creditModifier;

        if(requiredPeriod <= LoanValidator.MAX_LOAN_PERIOD){
            int finalPeriod = Math.max(requiredPeriod, LoanValidator.MIN_LOAN_PERIOD);
            int finalAmount = Math.min(creditModifier * finalPeriod, LoanValidator.MAX_LOAN_AMOUNT);
            return LoanDecisionReportDto.builder()
                    .outcome(DecisionEngineOutcome.POSITIVE)
                    .amount(finalAmount)
                    .period(finalPeriod)
                    .build();
        }

        return new LoanDecisionReportDto(DecisionEngineOutcome.NEGATIVE, 0, loanInfo.period());
    }
}
