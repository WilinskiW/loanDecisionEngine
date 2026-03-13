package com.task.decisionengine.domain;

import com.task.decisionengine.infrastructure.InMemoryUserCreditRegistry;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanDecisionEngineTest {

    @Test
    public void should_throw_exception_when_loan_amount_is_lower_than_min(){
        //given
        LoanDecisionEngine loanDecisionEngine = new LoanDecisionEngine(new InMemoryUserCreditRegistry());
        LoanRequest loanInfoToReviewDto = LoanRequest.builder()
                .personalCode("49002010965")
                .amount(new BigDecimal("300"))
                .period(10)
                .build();
        //when & then
        assertThatThrownBy(() -> loanDecisionEngine.decide(loanInfoToReviewDto))
                .isInstanceOf(LoanValidationException.class)
                .hasMessage("Loan amount must be between 2000 and 10000");
    }

    @Test
    public void should_throw_exception_when_loan_amount_is_higher_than_max(){
        //given
        LoanDecisionEngine loanDecisionEngine = new LoanDecisionEngine(new InMemoryUserCreditRegistry());
        LoanRequest loanInfoToReviewDto = LoanRequest.builder()
                .personalCode("49002010965")
                .amount(new BigDecimal("10001"))
                .period(30)
                .build();
        //when & then
        assertThatThrownBy(() -> loanDecisionEngine.decide(loanInfoToReviewDto))
                .isInstanceOf(LoanValidationException.class)
                .hasMessage("Loan amount must be between 2000 and 10000");
    }

    @Test
    public void should_throw_exception_when_loan_period_is_lower_than_min(){
        //given
        LoanDecisionEngine loanDecisionEngine = new LoanDecisionEngine(new InMemoryUserCreditRegistry());
        LoanRequest loanInfoToReviewDto = LoanRequest.builder()
                .personalCode("49002010965")
                .amount(new BigDecimal("2500"))
                .period(11)
                .build();
        //when & then
        assertThatThrownBy(() -> loanDecisionEngine.decide(loanInfoToReviewDto))
                .isInstanceOf(LoanValidationException.class)
                .hasMessage("Loan period must be between 12 and 60");
    }

    @Test
    public void should_throw_exception_when_loan_period_is_higher_than_max(){
        //given
        LoanDecisionEngine loanDecisionEngine = new LoanDecisionEngine(new InMemoryUserCreditRegistry());
        LoanRequest loanInfoToReviewDto = LoanRequest.builder()
                .personalCode("49002010965")
                .amount(new BigDecimal("2500"))
                .period(61)
                .build();
        //when & then
        assertThatThrownBy(() -> loanDecisionEngine.decide(loanInfoToReviewDto))
                .isInstanceOf(LoanValidationException.class)
                .hasMessage("Loan period must be between 12 and 60");
    }

    @Test
    public void should_give_no_loan_when_user_has_debt() {
        //given
        LoanDecisionEngine loanDecisionEngine = new LoanDecisionEngine(new InMemoryUserCreditRegistry());
        LoanRequest loanInfoToReviewDto = LoanRequest.builder()
                .personalCode("49002010965")
                .amount(new BigDecimal("2500"))
                .period(30)
                .build();
        //when
        LoanOffer decisionReportDto = loanDecisionEngine.decide(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionOutcome.NEGATIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void should_accept_user_loan_and_give_maximum_10000_when_it_exceed_the_maximum_amount(){
        //given
        LoanDecisionEngine loanDecisionEngine = new LoanDecisionEngine(new InMemoryUserCreditRegistry());
        LoanRequest loanInfoToReviewDto = LoanRequest.builder()
                .personalCode("49002010998")
                .amount(new BigDecimal("4000"))
                .period(15)
                .build();
        //when
        LoanOffer decisionReportDto = loanDecisionEngine.decide(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionOutcome.POSITIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(BigDecimal.valueOf(10000));
    }

    @Test
    public void should_extend_loan_period_when_user_want_loan_for_too_short_time_period(){
        //given
        LoanDecisionEngine loanDecisionEngine = new LoanDecisionEngine(new InMemoryUserCreditRegistry());
        LoanRequest loanInfoToReviewDto = LoanRequest.builder()
                .personalCode("49002010976")
                .amount(new BigDecimal("2000"))
                .period(12)
                .build();
        //when
        LoanOffer decisionReportDto = loanDecisionEngine.decide(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionOutcome.POSITIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(BigDecimal.valueOf(2000));
        assertThat(decisionReportDto.period()).isEqualTo(20);
    }

    @Test
    public void should_increase_amount_and_keep_period_when_credit_modifier_is_low(){
        //given
        LoanDecisionEngine loanDecisionEngine = new LoanDecisionEngine(new InMemoryUserCreditRegistry());
        LoanRequest loanInfoToReviewDto = LoanRequest.builder()
                .personalCode("49002010976")
                .amount(new BigDecimal("2000"))
                .period(55)
                .build();
        //when
        LoanOffer decisionReportDto = loanDecisionEngine.decide(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionOutcome.POSITIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(BigDecimal.valueOf(5500));
        assertThat(decisionReportDto.period()).isEqualTo(55);
    }

    @Test
    public void should_decrease_amount_when_it_is_too_high(){
        //given
        LoanDecisionEngine loanDecisionEngine = new LoanDecisionEngine(new InMemoryUserCreditRegistry());
        LoanRequest loanInfoToReviewDto = LoanRequest.builder()
                .personalCode("49002010976")
                .amount(new BigDecimal("2500"))
                .period(25)
                .build();
        //when
        LoanOffer decisionReportDto = loanDecisionEngine.decide(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionOutcome.POSITIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(BigDecimal.valueOf(2500));
        assertThat(decisionReportDto.period()).isEqualTo(25);
    }

    @Test
    public void should_give_no_loan_when_user_has_low_credit_modifier(){
        //given
        String lowModifierUser = "1234567890";
        UserCreditRegistry testRegistry = personalCode -> lowModifierUser.equals(personalCode) ? 1 : 0;
        LoanDecisionEngine loanDecisionEngine = new LoanDecisionEngine(testRegistry);
        LoanRequest loanInfoToReviewDto = LoanRequest.builder()
                .personalCode("1234567890")
                .amount(new BigDecimal("2500"))
                .period(12)
                .build();
        //when
        LoanOffer decisionReportDto = loanDecisionEngine.decide(loanInfoToReviewDto);
        //then
        assertThat(decisionReportDto.outcome()).isEqualTo(DecisionOutcome.NEGATIVE);
        assertThat(decisionReportDto.amount()).isEqualTo(BigDecimal.ZERO);
    }
}