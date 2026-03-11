package com.task.decisionengine.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoanDecisionEngineFacadeTest {

    @Test
    public void should_give_no_loan_when_user_has_debt() {
        //given
        LoanDecisionEngineFacade loanDecisionEngineFacade = new LoanDecisionEngineFacade(new CreditScoreCalculator());
        UserLoanInfoToReviewDto loanInfoToReviewDto = UserLoanInfoToReviewDto.builder()
                .personalCode("49002010965")
                .loanAmount(300)
                .loanPeriod(10)
                .build();
        //when
        LoanDecisionReportDto decisionReportDto = loanDecisionEngineFacade.decideLoanAmount(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionEngineOutcome.NEGATIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(0);
    }
}