package com.task.decisionengine.domain;

import java.util.HashMap;
import java.util.Map;

class MockUserRegistry implements UserProfileRegistry {
    private static final Map<String, CreditSegment> DEFAULT_REGISTRY = Map.of(
            "49002010965", CreditSegment.DEBT,
            "49002010976", CreditSegment.SEGMENT_1,
            "49002010987", CreditSegment.SEGMENT_2,
            "49002010998", CreditSegment.SEGMENT_3
    );

    private final Map<String, CreditSegment> registry;

    MockUserRegistry() {
        this.registry = new HashMap<>(DEFAULT_REGISTRY);
    }

    MockUserRegistry(Map<String, CreditSegment> extraData) {
        this();
        this.registry.putAll(extraData);
    }

    @Override
    public int findUserCreditModifierByPersonalCode(String personalCode) {
        return registry.getOrDefault(personalCode, CreditSegment.DEBT).getCreditModifier();
    }
}
