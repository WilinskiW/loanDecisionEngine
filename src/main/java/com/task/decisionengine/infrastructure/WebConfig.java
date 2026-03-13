package com.task.decisionengine.infrastructure;

import com.task.decisionengine.domain.LoanDecisionEngine;
import com.task.decisionengine.domain.UserCreditRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    LoanDecisionEngine loanDecisionEngine(UserCreditRegistry userCreditRegistry) {
        return new LoanDecisionEngine(userCreditRegistry);
    }
}
