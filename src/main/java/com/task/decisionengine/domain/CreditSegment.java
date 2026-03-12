package com.task.decisionengine.domain;

enum CreditSegment {
    DEBT(0),
    SEGMENT_1(100),
    SEGMENT_2(300),
    SEGMENT_3(1000),
    // for test purposes
    CUSTOM_LOW(20);

    private final int creditModifier;

    CreditSegment(int creditModifier) {
        this.creditModifier = creditModifier;
    }

    int getCreditModifier() {
        return creditModifier;
    }
}
