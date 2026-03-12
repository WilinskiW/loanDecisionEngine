package com.task.decisionengine.infrastructure;

import com.task.decisionengine.domain.CreditSegment;
import com.task.decisionengine.domain.UserCreditRegistry;

import java.util.Map;

public class InMemoryUserCreditRegistry implements UserCreditRegistry {
    private final Map<String, CreditSegment> registry;

    public InMemoryUserCreditRegistry() {
        this.registry = Map.of(
                "49002010965", CreditSegment.DEBT,
                "49002010976", CreditSegment.SEGMENT_1,
                "49002010987", CreditSegment.SEGMENT_2,
                "49002010998", CreditSegment.SEGMENT_3
        );
    }

    @Override
    public int findCreditModifier(String personalCode) {
        return registry.getOrDefault(personalCode, CreditSegment.DEBT).getCreditModifier();
    }
}
