package com.task.decisionengine.domain;

import static com.task.decisionengine.domain.LoanOffer.buildNegativeOutcome;
import static com.task.decisionengine.domain.LoanOffer.buildPositiveOutcome;

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

        if (maxAmount >= LoanValidator.MIN_LOAN_AMOUNT.intValue()) {
            return buildPositiveOutcome(maxAmount, request.period());
        }

        int requiredPeriod = LoanValidator.MIN_LOAN_AMOUNT.intValue() / creditModifier;

        if(requiredPeriod <= LoanValidator.MAX_LOAN_PERIOD){
            int finalPeriod = Math.max(requiredPeriod, LoanValidator.MIN_LOAN_PERIOD);
            int finalAmount = Math.min(creditModifier * finalPeriod, LoanValidator.MAX_LOAN_AMOUNT.intValue());
            return buildPositiveOutcome(finalAmount, finalPeriod);
        }

        return buildNegativeOutcome(request.period());
    }
}
