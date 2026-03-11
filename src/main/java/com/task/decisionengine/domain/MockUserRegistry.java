package com.task.decisionengine.domain;

import java.util.Map;

class MockUserRegistry implements UserProfileRegistry {
    private final Map<String, CreditSegment> registry;

    public MockUserRegistry() {
        this.registry = Map.of(
                "49002010965", CreditSegment.DEBT,
                "49002010976", CreditSegment.SEGMENT_1,
                "49002010987", CreditSegment.SEGMENT_2,
                "49002010998", CreditSegment.SEGMENT_3
        );
    }

    @Override
    public double findUserCreditModifierByPersonalCode(String personalCode) {
        return registry.getOrDefault(personalCode, CreditSegment.DEBT).getCreditModifier();
    }
}
