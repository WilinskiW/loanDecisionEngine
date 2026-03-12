package com.task.decisionengine.domain;

class LoanDecisionEngineFactory {
    static LoanDecisionEngine createForTest(UserCreditRegistry userCreditRegistry) {
        return new LoanDecisionEngine(userCreditRegistry);
    }
}
