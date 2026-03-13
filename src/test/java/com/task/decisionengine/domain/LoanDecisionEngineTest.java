package com.task.decisionengine.domain;

import com.task.decisionengine.infrastructure.InMemoryUserCreditRegistry;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanDecisionEngineTest {
    private final LoanDecisionEngine engine = new LoanDecisionEngine(new InMemoryUserCreditRegistry());

    private LoanRequest buildLoanRequest(String personalCode, String amount, int period) {
        return LoanRequest.builder()
                .personalCode(personalCode)
                .amount(new BigDecimal(amount))
                .period(period)
                .build();
    }

    @Test
    public void should_throw_exception_when_loan_amount_is_lower_than_min(){
        //given
        LoanRequest request = buildLoanRequest("49002010965", "30", 10);
        //when & then
        assertThatThrownBy(() -> engine.decide(request))
                .isInstanceOf(LoanValidationException.class)
                .hasMessage("Loan amount must be between 2000 and 10000");
    }

    @Test
    public void should_throw_exception_when_loan_amount_is_higher_than_max(){
        //given
        LoanRequest request = buildLoanRequest("49002010965", "10001", 30);
        //when & then
        assertThatThrownBy(() -> engine.decide(request))
                .isInstanceOf(LoanValidationException.class)
                .hasMessage("Loan amount must be between 2000 and 10000");
    }

    @Test
    public void should_throw_exception_when_loan_period_is_lower_than_min(){
        //given
        LoanRequest request = buildLoanRequest("49002010965", "2500", 11);
        //when & then
        assertThatThrownBy(() -> engine.decide(request))
                .isInstanceOf(LoanValidationException.class)
                .hasMessage("Loan period must be between 12 and 60");
    }

    @Test
    public void should_throw_exception_when_loan_period_is_higher_than_max(){
        //given
        LoanRequest request = buildLoanRequest("49002010965", "2500", 61);
        //when & then
        assertThatThrownBy(() -> engine.decide(request))
                .isInstanceOf(LoanValidationException.class)
                .hasMessage("Loan period must be between 12 and 60");
    }

    @Test
    public void should_give_no_loan_when_user_has_debt() {
        //given
        LoanRequest request = buildLoanRequest("49002010965", "2500", 30);
        //when
        LoanOffer offer = engine.decide(request);
        //then
        assertThat(offer.outcome()).isEqualTo(DecisionOutcome.NEGATIVE);
        assertThat(offer.amount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void should_accept_user_loan_and_give_maximum_10000_when_it_exceeds_the_maximum_amount(){
        //given
        LoanRequest request = buildLoanRequest("49002010998", "4000", 15);
        //when
        LoanOffer offer = engine.decide(request);
        //then
        assertThat(offer.outcome()).isEqualTo(DecisionOutcome.POSITIVE);
        assertThat(offer.amount()).isEqualTo(BigDecimal.valueOf(10000));
    }

    @Test
    public void should_extend_loan_period_when_user_wants_a_loan_for_too_short_time_period(){
        //given
        LoanRequest request = buildLoanRequest("49002010976", "2000", 12);
        //when
        LoanOffer offer = engine.decide(request);
        //then
        assertThat(offer.outcome()).isEqualTo(DecisionOutcome.POSITIVE);
        assertThat(offer.amount()).isEqualTo(BigDecimal.valueOf(2000));
        assertThat(offer.period()).isEqualTo(20);
    }

    @Test
    public void should_increase_amount_and_keep_period_when_credit_modifier_is_low(){
        //given
        LoanRequest request = buildLoanRequest("49002010976", "2000", 55);
        //when
        LoanOffer offer = engine.decide(request);
        //then
        assertThat(offer.outcome()).isEqualTo(DecisionOutcome.POSITIVE);
        assertThat(offer.amount()).isEqualTo(BigDecimal.valueOf(5500));
        assertThat(offer.period()).isEqualTo(55);
    }

    @Test
    public void should_decrease_amount_when_it_is_too_high(){
        //given
        LoanRequest request = buildLoanRequest("49002010976", "2500", 25);
        //when
        LoanOffer offer = engine.decide(request);
        //then
        assertThat(offer.outcome()).isEqualTo(DecisionOutcome.POSITIVE);
        assertThat(offer.amount()).isEqualTo(BigDecimal.valueOf(2500));
        assertThat(offer.period()).isEqualTo(25);
    }

    @Test
    public void should_give_no_loan_when_user_has_low_credit_modifier(){
        //given
        String lowModifierUser = "1234567890";
        UserCreditRegistry testRegistry = personalCode -> lowModifierUser.equals(personalCode) ? 1 : 0;
        LoanDecisionEngine loanEngine = new LoanDecisionEngine(testRegistry);
        LoanRequest request = buildLoanRequest("1234567890", "2500", 12);
        //when
        LoanOffer offer = loanEngine.decide(request);
        //then
        assertThat(offer.outcome()).isEqualTo(DecisionOutcome.NEGATIVE);
        assertThat(offer.amount()).isEqualTo(BigDecimal.ZERO);
    }
}