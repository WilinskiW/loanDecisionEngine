package com.task.decisionengine.domain;

class CreditScoreCalculator {
    private final UserProfileRegistry userProfileRegistry;

    CreditScoreCalculator() {
        this.userProfileRegistry = new MockUserRegistry();
    }

    LoanDecisionReportDto calculate(UserLoanInfoToReviewDto loanInfo) {
        int creditModifier = userProfileRegistry.findUserCreditModifierByPersonalCode(loanInfo.personalCode());
        int maxAmount = creditModifier * loanInfo.period();

        if (maxAmount >= LoanValidator.MIN_LOAN_AMOUNT) {
            return LoanDecisionReportDto.builder()
                    .outcome(DecisionEngineOutcome.POSITIVE)
                    .amount(Math.min(maxAmount, LoanValidator.MAX_LOAN_AMOUNT))
                    .build();
        }

        //todo
        int newPeriod = LoanValidator.MIN_LOAN_AMOUNT / creditModifier;
        return new LoanDecisionReportDto(DecisionEngineOutcome.NEGATIVE, 0);
    }
}
