package com.task.decisionengine.infrastructure.config;

import com.task.decisionengine.domain.LoanDecisionEngine;
import com.task.decisionengine.domain.UserCreditRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    LoanDecisionEngine loanDecisionEngine(UserCreditRegistry userCreditRegistry) {
        return new LoanDecisionEngine(userCreditRegistry);
    }
}
