package com.task.decisionengine.domain;

class LoanDecisionEngineFacadeConfiguration {
    static LoanDecisionEngineFacade createForTest(UserProfileRegistry userProfileRegistry) {
        return new LoanDecisionEngineFacade(new CreditScoreCalculator(userProfileRegistry));
    }
}
