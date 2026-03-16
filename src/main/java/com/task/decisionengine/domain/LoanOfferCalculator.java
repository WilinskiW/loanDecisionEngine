package com.task.decisionengine.domain;

import static com.task.decisionengine.domain.LoanOffer.buildNegativeOutcome;
import static com.task.decisionengine.domain.LoanOffer.buildPositiveOutcome;

class LoanOfferCalculator {
    private final UserCreditRegistry userCreditRegistry;

    public LoanOfferCalculator(UserCreditRegistry userCreditRegistry) {
        this.userCreditRegistry = userCreditRegistry;
    }

    LoanOffer calculate(LoanRequest request) {
        int modifier = userCreditRegistry.findCreditModifier(request.personalCode());

        if (modifier <= 0) {
            return buildNegativeOutcome(request.period());
        }

        int maxAmount = modifier * request.period();

        if (maxAmount >= Loan.MIN_LOAN_AMOUNT.intValue()) {
            return buildPositiveOutcome(maxAmount, request.period());
        }

        return findBestAlternative(modifier, request);
    }

    private LoanOffer findBestAlternative(int modifier, LoanRequest request){
        int requiredPeriod = Loan.MIN_LOAN_AMOUNT.intValue() / modifier;

        if(isPeriodWithinRange(requiredPeriod)){
            int finalPeriod = Math.max(requiredPeriod, Loan.MIN_LOAN_PERIOD);
            int finalAmount = Math.min(modifier * finalPeriod, Loan.MAX_LOAN_AMOUNT.intValue());
            return buildPositiveOutcome(finalAmount, finalPeriod);
        }

        return buildNegativeOutcome(request.period());
    }

    private boolean isPeriodWithinRange(int period){
        return period >= Loan.MIN_LOAN_PERIOD && period <= Loan.MAX_LOAN_PERIOD;
    }
}
