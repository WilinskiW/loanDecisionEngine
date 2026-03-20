package com.task.decisionengine.domain;

import static com.task.decisionengine.domain.LoanOffer.buildNegativeOutcome;
import static com.task.decisionengine.domain.LoanOffer.buildPositiveOutcome;

class LoanOfferCalculator {
    private final UserCreditRegistry userCreditRegistry;

    public LoanOfferCalculator(UserCreditRegistry userCreditRegistry) {
        this.userCreditRegistry = userCreditRegistry;
    }

    LoanOffer calculate(Loan requestedLoan, String personalCode) {
        int modifier = userCreditRegistry.getCreditModifier(personalCode);

        if (isIneligible(modifier)) {
            return buildNegativeOutcome(requestedLoan.getPeriod());
        }

        int maxAmount = calculateMaxAmount(modifier, requestedLoan.getPeriod());

        if (isLoanAffordable(maxAmount)) {
            return buildPositiveOutcome(maxAmount, requestedLoan.getPeriod());
        }

        return findAlternativeOffer(modifier, requestedLoan.getPeriod());
    }

    private boolean isIneligible(int modifier){
        return modifier <= 0;
    }

    private int calculateMaxAmount(int modifier, int period){
        return modifier * period;
    }

    private boolean isLoanAffordable(int maxAmount){
        return maxAmount >= Loan.MIN_AMOUNT.intValue();
    }

    private LoanOffer findAlternativeOffer(int modifier, int requestedPeriod){
        int requiredPeriod = calculateRequiredPeriod(modifier);

        if(isPeriodInRange(requiredPeriod)){
            return calculateFinalOffer(modifier, requiredPeriod);
        }

        return buildNegativeOutcome(requestedPeriod);
    }

    private int calculateRequiredPeriod(int modifier){
        return Loan.MIN_AMOUNT.intValue() / modifier;
    }

    private boolean isPeriodInRange(int period){
        return period >= Loan.MIN_PERIOD && period <= Loan.MAX_PERIOD;
    }

    private LoanOffer calculateFinalOffer(int maxAmount, int period){
        int finalPeriod = Math.max(period, Loan.MIN_PERIOD);
        int finalAmount = Math.min(calculateMaxAmount(maxAmount, finalPeriod), Loan.MAX_AMOUNT.intValue());
        return buildPositiveOutcome(finalAmount, finalPeriod);
    }
}
