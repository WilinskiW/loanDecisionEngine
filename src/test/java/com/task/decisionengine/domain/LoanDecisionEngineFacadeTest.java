package com.task.decisionengine.domain;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanDecisionEngineFacadeTest {

    @Test
    public void should_throw_exception_when_loan_amount_is_lower_than_min(){
        //given
        LoanDecisionEngineFacade loanDecisionEngineFacade = LoanDecisionEngineFacadeConfiguration.createForTest(new MockUserRegistry());
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
        LoanDecisionEngineFacade loanDecisionEngineFacade = LoanDecisionEngineFacadeConfiguration.createForTest(new MockUserRegistry());
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
        LoanDecisionEngineFacade loanDecisionEngineFacade = LoanDecisionEngineFacadeConfiguration.createForTest(new MockUserRegistry());
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
        LoanDecisionEngineFacade loanDecisionEngineFacade = LoanDecisionEngineFacadeConfiguration.createForTest(new MockUserRegistry());
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
        LoanDecisionEngineFacade loanDecisionEngineFacade = LoanDecisionEngineFacadeConfiguration.createForTest(new MockUserRegistry());
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
    public void should_accept_user_loan_and_give_maximum_10000_when_it_exceed_the_maximum_amount(){
        //given
        LoanDecisionEngineFacade loanDecisionEngineFacade = LoanDecisionEngineFacadeConfiguration.createForTest(new MockUserRegistry());
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

    @Test
    public void should_extend_loan_period_when_user_want_loan_for_too_short_time_period(){
        //given
        LoanDecisionEngineFacade loanDecisionEngineFacade = LoanDecisionEngineFacadeConfiguration.createForTest(new MockUserRegistry());
        UserLoanInfoToReviewDto loanInfoToReviewDto = UserLoanInfoToReviewDto.builder()
                .personalCode("49002010976")
                .amount(2000)
                .period(12)
                .build();
        //when
        LoanDecisionReportDto decisionReportDto = loanDecisionEngineFacade.decideLoanAmount(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionEngineOutcome.POSITIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(2000);
        assertThat(decisionReportDto.period()).isEqualTo(20);
    }

    @Test
    public void should_increase_amount_and_keep_period_when_credit_modifier_is_low(){
        //given
        LoanDecisionEngineFacade loanDecisionEngineFacade = LoanDecisionEngineFacadeConfiguration.createForTest(new MockUserRegistry());
        UserLoanInfoToReviewDto loanInfoToReviewDto = UserLoanInfoToReviewDto.builder()
                .personalCode("49002010976")
                .amount(2000)
                .period(55)
                .build();
        //when
        LoanDecisionReportDto decisionReportDto = loanDecisionEngineFacade.decideLoanAmount(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionEngineOutcome.POSITIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(5500);
        assertThat(decisionReportDto.period()).isEqualTo(55);
    }

    @Test
    public void should_decrease_amount_when_it_is_too_high(){
        //given
        LoanDecisionEngineFacade loanDecisionEngineFacade = LoanDecisionEngineFacadeConfiguration.createForTest(new MockUserRegistry());
        UserLoanInfoToReviewDto loanInfoToReviewDto = UserLoanInfoToReviewDto.builder()
                .personalCode("49002010976")
                .amount(4000)
                .period(25)
                .build();
        //when
        LoanDecisionReportDto decisionReportDto = loanDecisionEngineFacade.decideLoanAmount(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionEngineOutcome.POSITIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(2500);
        assertThat(decisionReportDto.period()).isEqualTo(25);
    }

    @Test
    public void should_give_no_loan_when_user_has_low_credit_modifier(){
        //given
        String lowModifierUser = "1234567890";
        CreditSegment veryLowSegment = CreditSegment.CUSTOM_LOW;
        UserProfileRegistry testRegistry = new MockUserRegistry(Map.of(lowModifierUser, veryLowSegment));
        LoanDecisionEngineFacade loanDecisionEngineFacade = LoanDecisionEngineFacadeConfiguration.createForTest(testRegistry);
        UserLoanInfoToReviewDto loanInfoToReviewDto = UserLoanInfoToReviewDto.builder()
                .personalCode("1234567890")
                .amount(2000)
                .period(12)
                .build();
        //when
        LoanDecisionReportDto decisionReportDto = loanDecisionEngineFacade.decideLoanAmount(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionEngineOutcome.NEGATIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(0);
    }
}