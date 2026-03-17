package com.task.decisionengine.domain;

import static com.task.decisionengine.domain.LoanOffer.buildNegativeOutcome;
import static com.task.decisionengine.domain.LoanOffer.buildPositiveOutcome;

class LoanOfferCalculator {
    private final UserCreditRegistry userCreditRegistry;

    public LoanOfferCalculator(UserCreditRegistry userCreditRegistry) {
        this.userCreditRegistry = userCreditRegistry;
    }

    LoanOffer calculate(LoanRequest request) {
        int modifier = userCreditRegistry.getCreditModifier(request.personalCode());

        if (isIneligible(modifier)) {
            return buildNegativeOutcome(request.period());
        }

        int maxAmount = calculateMaxAmount(modifier, request.period());

        if (isLoanAffordable(maxAmount)) {
            return buildPositiveOutcome(maxAmount, request.period());
        }

        return findAlternativeOffer(modifier, request.period());
    }

    private boolean isIneligible(int modifier){
        return modifier <= 0;
    }

    private int calculateMaxAmount(int modifier, int period){
        return modifier * period;
    }

    private boolean isLoanAffordable(int maxAmount){
        return maxAmount >= LoanValidator.MIN_LOAN_AMOUNT.intValue();
    }

    private LoanOffer findAlternativeOffer(int modifier, int requestedPeriod){
        int requiredPeriod = calculateRequiredPeriod(requestedPeriod);

        if(isPeriodInRange(requiredPeriod)){
            return calculateFinalOffer(modifier, requiredPeriod);
        }

        return buildNegativeOutcome(requestedPeriod);
    }

    private int calculateRequiredPeriod(int modifier){
        return LoanValidator.MIN_LOAN_AMOUNT.intValue() / modifier;
    }

    private boolean isPeriodInRange(int period){
        return period >= LoanValidator.MIN_LOAN_PERIOD && period <= LoanValidator.MAX_LOAN_PERIOD;
    }

    private LoanOffer calculateFinalOffer(int maxAmount, int period){
        int finalPeriod = Math.max(period, LoanValidator.MIN_LOAN_PERIOD);
        int finalAmount = Math.min(calculateMaxAmount(maxAmount, finalPeriod), LoanValidator.MAX_LOAN_AMOUNT.intValue());
        return buildPositiveOutcome(finalAmount, finalPeriod);
    }
}
