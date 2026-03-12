package com.task.decisionengine.domain;

class CreditScoreCalculator {
    private final UserProfileRegistry userProfileRegistry;

    CreditScoreCalculator(UserProfileRegistry userProfileRegistry) {
        this.userProfileRegistry = userProfileRegistry;
    }

    LoanDecisionReportDto calculate(UserLoanInfoToReviewDto loanInfo) {
        int creditModifier = userProfileRegistry.findUserCreditModifierByPersonalCode(loanInfo.personalCode());

        if (creditModifier <= 0) {
            return buildNegativeOutcome(loanInfo.period());
        }

        int maxAmount = creditModifier * loanInfo.period();

        if (maxAmount >= LoanValidator.MIN_LOAN_AMOUNT) {
            return buildPositiveOutcome(maxAmount, loanInfo.period());
        }

        int requiredPeriod = LoanValidator.MIN_LOAN_AMOUNT / creditModifier;

        if(requiredPeriod <= LoanValidator.MAX_LOAN_PERIOD){
            int finalPeriod = Math.max(requiredPeriod, LoanValidator.MIN_LOAN_PERIOD);
            int finalAmount = Math.min(creditModifier * finalPeriod, LoanValidator.MAX_LOAN_AMOUNT);
            return buildPositiveOutcome(finalAmount, finalPeriod);
        }

        return buildNegativeOutcome(loanInfo.period());
    }

    private LoanDecisionReportDto buildNegativeOutcome(int period){
        return new LoanDecisionReportDto(DecisionEngineOutcome.NEGATIVE, 0, period);
    }

    private LoanDecisionReportDto buildPositiveOutcome(int amount, int period){
        return LoanDecisionReportDto.builder()
                .outcome(DecisionEngineOutcome.POSITIVE)
                .amount(Math.min(amount, LoanValidator.MAX_LOAN_AMOUNT))
                .period(period)
                .build();
    }

}
