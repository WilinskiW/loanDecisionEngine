package com.task.decisionengine.domain;

import java.math.BigDecimal;

import static com.task.decisionengine.domain.Loan.calculateMaxAmount;
import static com.task.decisionengine.domain.Loan.isLoanAffordable;
import static com.task.decisionengine.domain.LoanOffer.buildNegativeOutcome;
import static com.task.decisionengine.domain.LoanOffer.buildPositiveOutcome;

class LoanOfferCalculator {
    private final UserCreditRegistry userCreditRegistry;

    public LoanOfferCalculator(UserCreditRegistry userCreditRegistry) {
        this.userCreditRegistry = userCreditRegistry;
    }

    LoanOffer calculate(LoanRequest request) {
        int modifier = userCreditRegistry.findCreditModifier(request.personalCode());

        if (isIneligible(modifier)) {
            return buildNegativeOutcome(request.period());
        }

        var maxAmountForRequestedPeriod = calculateMaxAmount(modifier, request.period());

        if (isLoanAffordable(maxAmountForRequestedPeriod)) {
            return buildPositiveOutcome(maxAmountForRequestedPeriod, request.period());
        }

        return findAlternativeOffer(modifier);
    }

    private boolean isIneligible(int modifier) {
        return modifier <= 0;
    }

    private LoanOffer findAlternativeOffer(int modifier) {
        int requiredPeriod = (int) Math.ceil(Loan.MIN_AMOUNT.doubleValue() / modifier);

        if (isPeriodValid(requiredPeriod)) {
            BigDecimal amount = calculateMaxAmount(modifier, requiredPeriod);
            return buildPositiveOutcome(amount, requiredPeriod);
        }

        return buildNegativeOutcome(Loan.MIN_PERIOD);
    }

    private boolean isPeriodValid(int period) {
        return period >= Loan.MIN_PERIOD && period <= Loan.MAX_PERIOD;
    }
}
