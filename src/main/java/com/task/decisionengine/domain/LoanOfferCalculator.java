package com.task.decisionengine.domain;

class LoanOfferCalculator {
    private final UserCreditRegistry userCreditRegistry;

    public LoanOfferCalculator(UserCreditRegistry userCreditRegistry) {
        this.userCreditRegistry = userCreditRegistry;
    }

    LoanOffer calculate(LoanRequest request) {
        int creditModifier = userCreditRegistry.findCreditModifier(request.personalCode());

        if (creditModifier <= 0) {
            return buildNegativeOutcome(request.period());
        }

        int maxAmount = creditModifier * request.period();

        if (maxAmount >= LoanRequestValidator.MIN_LOAN_AMOUNT) {
            return buildPositiveOutcome(maxAmount, request.period());
        }

        int requiredPeriod = LoanRequestValidator.MIN_LOAN_AMOUNT / creditModifier;

        if(requiredPeriod <= LoanRequestValidator.MAX_LOAN_PERIOD){
            int finalPeriod = Math.max(requiredPeriod, LoanRequestValidator.MIN_LOAN_PERIOD);
            int finalAmount = Math.min(creditModifier * finalPeriod, LoanRequestValidator.MAX_LOAN_AMOUNT);
            return buildPositiveOutcome(finalAmount, finalPeriod);
        }

        return buildNegativeOutcome(request.period());
    }

    private LoanOffer buildNegativeOutcome(int period){
        return new LoanOffer(DecisionOutcome.NEGATIVE, 0, period);
    }

    private LoanOffer buildPositiveOutcome(int amount, int period){
        return LoanOffer.builder()
                .outcome(DecisionOutcome.POSITIVE)
                .amount(Math.min(amount, LoanRequestValidator.MAX_LOAN_AMOUNT))
                .period(period)
                .build();
    }

}
