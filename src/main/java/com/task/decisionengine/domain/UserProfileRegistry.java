package com.task.decisionengine.domain;

interface UserProfileRegistry {
    int findUserCreditModifierByPersonalCode(String personalCode);
}
