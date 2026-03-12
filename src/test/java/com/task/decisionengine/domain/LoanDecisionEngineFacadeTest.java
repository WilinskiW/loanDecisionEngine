package com.task.decisionengine.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanDecisionEngineFacadeTest {

    @Test
    public void should_throw_exception_when_loan_amount_is_lower_than_min(){
        //given
        LoanDecisionEngineFacade loanDecisionEngineFacade = new LoanDecisionEngineFacade(new CreditScoreCalculator());
        UserLoanInfoToReviewDto loanInfoToReviewDto = UserLoanInfoToReviewDto.builder()
                .personalCode("49002010965")
                .amount(300)
                .period(10)
                .build();
        //when & then
        assertThatThrownBy(() -> loanDecisionEngineFacade.decideLoanAmount(loanInfoToReviewDto))
                .isInstanceOf(LoanValidationException.class)
                .hasMessage("Loan amount must be between 2000 and 10000");
    }

    @Test
    public void should_throw_exception_when_loan_amount_is_higher_than_max(){
        //given
        LoanDecisionEngineFacade loanDecisionEngineFacade = new LoanDecisionEngineFacade(new CreditScoreCalculator());
        UserLoanInfoToReviewDto loanInfoToReviewDto = UserLoanInfoToReviewDto.builder()
                .personalCode("49002010965")
                .amount(10001)
                .period(30)
                .build();
        //when & then
        assertThatThrownBy(() -> loanDecisionEngineFacade.decideLoanAmount(loanInfoToReviewDto))
                .isInstanceOf(LoanValidationException.class)
                .hasMessage("Loan amount must be between 2000 and 10000");
    }

    @Test
    public void should_throw_exception_when_loan_period_is_lower_than_min(){
        //given
        LoanDecisionEngineFacade loanDecisionEngineFacade = new LoanDecisionEngineFacade(new CreditScoreCalculator());
        UserLoanInfoToReviewDto loanInfoToReviewDto = UserLoanInfoToReviewDto.builder()
                .personalCode("49002010965")
                .amount(2500)
                .period(11)
                .build();
        //when & then
        assertThatThrownBy(() -> loanDecisionEngineFacade.decideLoanAmount(loanInfoToReviewDto))
                .isInstanceOf(LoanValidationException.class)
                .hasMessage("Loan period must be between 12 and 60");
    }

    @Test
    public void should_throw_exception_when_loan_period_is_higher_than_max(){
        //given
        LoanDecisionEngineFacade loanDecisionEngineFacade = new LoanDecisionEngineFacade(new CreditScoreCalculator());
        UserLoanInfoToReviewDto loanInfoToReviewDto = UserLoanInfoToReviewDto.builder()
                .personalCode("49002010965")
                .amount(2500)
                .period(61)
                .build();
        //when & then
        assertThatThrownBy(() -> loanDecisionEngineFacade.decideLoanAmount(loanInfoToReviewDto))
                .isInstanceOf(LoanValidationException.class)
                .hasMessage("Loan period must be between 12 and 60");
    }

    @Test
    public void should_give_no_loan_when_user_has_debt() {
        //given
        LoanDecisionEngineFacade loanDecisionEngineFacade = new LoanDecisionEngineFacade(new CreditScoreCalculator());
        UserLoanInfoToReviewDto loanInfoToReviewDto = UserLoanInfoToReviewDto.builder()
                .personalCode("49002010965")
                .amount(2500)
                .period(30)
                .build();
        //when
        LoanDecisionReportDto decisionReportDto = loanDecisionEngineFacade.decideLoanAmount(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionEngineOutcome.NEGATIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(0);
    }

    @Test
    public void should_accept_user_loan_and_give_maximum_amount_within_constraints_range(){
        //given
        LoanDecisionEngineFacade loanDecisionEngineFacade = new LoanDecisionEngineFacade(new CreditScoreCalculator());
        UserLoanInfoToReviewDto loanInfoToReviewDto = UserLoanInfoToReviewDto.builder()
                .personalCode("49002010998")
                .amount(4000)
                .period(15)
                .build();
        //when
        LoanDecisionReportDto decisionReportDto = loanDecisionEngineFacade.decideLoanAmount(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionEngineOutcome.POSITIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(10_000);
    }
}