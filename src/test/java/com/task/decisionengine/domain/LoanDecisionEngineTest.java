package com.task.decisionengine.domain;

import com.task.decisionengine.infrastructure.registry.InMemoryUserCreditRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
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

    @Nested
    class RequestValidation {

        @ParameterizedTest
        @ValueSource(strings = {"1999", "10001"})
        @DisplayName("Should throw exception when amount is out of bounds")
        public void should_throw_exception_when_amount_is_out_of_bounds(String invalidAmount) {
            //given
            LoanRequest request = buildLoanRequest("49002010965", invalidAmount, 30);

            //when & then
            assertThatThrownBy(() -> engine.decide(request))
                    .isInstanceOf(LoanValidationException.class)
                    .hasMessage("Loan amount must be between 2000 and 10000");
        }

        @ParameterizedTest
        @ValueSource(ints = {11, 61})
        @DisplayName("Should throw exception when period is out of bounds")
        public void should_throw_exception_when_period_is_out_of_bounds(int invalidPeriod) {
            //given
            LoanRequest request = buildLoanRequest("49002010965", "2500", invalidPeriod);

            //when & then
            assertThatThrownBy(() -> engine.decide(request))
                    .isInstanceOf(LoanValidationException.class)
                    .hasMessage("Loan period must be between 12 and 60");
        }

        @ParameterizedTest
        @CsvSource({
                "2000, 30",
                "10000, 30",
                "5000, 12",
                "5000, 60"
        })
        @DisplayName("Should accept valid boundary values")
        public void should_accept_boundary_values(String amount, int period) {
            //given
            LoanRequest request = buildLoanRequest("49002010965", amount, period);

            //when & then
            assertThatNoException().isThrownBy(() -> engine.decide(request));
        }
    }

    @Nested
    class NegativeOutcomes {
        @Test
        @DisplayName("Should return negative decision when user has debt")
        void should_return_negative_decision_when_user_has_debt() {
            //given
            LoanRequest request = buildLoanRequest("49002010965", "2500", 30);
            //when
            LoanOffer offer = engine.decide(request);
            //then
            assertThat(offer.outcome()).isEqualTo(DecisionOutcome.NEGATIVE);
            assertThat(offer.amount()).isEqualTo(BigDecimal.ZERO);
            assertThat(offer.period()).isEqualTo(30);
        }

        @Test
        @DisplayName("Should return negative decision when user is not found in registry")
        void should_return_negative_decision_when_user_is_not_found_in_registry() {
            //given
            LoanRequest request = buildLoanRequest("72375010969", "2500", 30);
            //when
            LoanOffer offer = engine.decide(request);
            //then
            assertThat(offer.outcome()).isEqualTo(DecisionOutcome.NEGATIVE);
            assertThat(offer.amount()).isEqualTo(BigDecimal.ZERO);
            assertThat(offer.period()).isEqualTo(30);
        }

        @Test
        @DisplayName("Should return negative decision when user has low credit modifier")
        void should_return_negative_decision_when_user_has_low_credit_modifier() {
            //given
            String lowModifierUser = "1234567890";
            UserCreditRegistry testRegistry = personalCode -> lowModifierUser.equals(personalCode) ? 1 : -1;
            LoanDecisionEngine loanEngine = new LoanDecisionEngine(testRegistry);
            LoanRequest request = buildLoanRequest("1234567890", "2500", 12);
            //when
            LoanOffer offer = loanEngine.decide(request);
            //then
            assertThat(offer.outcome()).isEqualTo(DecisionOutcome.NEGATIVE);
            assertThat(offer.amount()).isEqualTo(BigDecimal.ZERO);
            assertThat(offer.period()).isEqualTo(12);
        }
    }

    @Nested
    class OfferCalculation {
        @Test
        @DisplayName("Should return offer for requested amount when credit score is exactly one")
        void should_return_offer_for_requested_amount_when_credit_score_is_exactly_one() {
            //given
            LoanRequest request = buildLoanRequest("49002010976", "2000", 20);
            //when
            LoanOffer offer = engine.decide(request);
            //then
            assertThat(offer.outcome()).isEqualTo(DecisionOutcome.POSITIVE);
            assertThat(offer.amount()).isEqualTo(BigDecimal.valueOf(2000));
            assertThat(offer.period()).isEqualTo(20);
        }

        @Test
        @DisplayName("Should return higher offer when credit score is above one")
        void should_return_higher_offer_when_credit_score_is_above_one() {
            //given
            LoanRequest request = buildLoanRequest("49002010976", "2000", 30);
            //when
            LoanOffer offer = engine.decide(request);
            //then
            assertThat(offer.outcome()).isEqualTo(DecisionOutcome.POSITIVE);
            assertThat(offer.amount()).isEqualTo(BigDecimal.valueOf(3000));
            assertThat(offer.period()).isEqualTo(30);
        }

        @Test
        @DisplayName("Should cap approved amount at maximum allowed sum")
        void should_cap_approved_amount_at_maximum_allowed_sum() {
            //given
            LoanRequest request = buildLoanRequest("49002010998", "4000", 15);
            //when
            LoanOffer offer = engine.decide(request);
            //then
            assertThat(offer.outcome()).isEqualTo(DecisionOutcome.POSITIVE);
            assertThat(offer.amount()).isEqualTo(BigDecimal.valueOf(10000));
        }

        @Test
        @DisplayName("Should increase amount and keep period when credit modifier is low")
        void should_increase_amount_and_keep_period_when_credit_modifier_is_low() {
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
        @DisplayName("Should return largest approvable amount when requested amount cannot be approved")
        void should_return_largest_approvable_amount_when_requested_amount_cannot_be_approved() {
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
        @DisplayName("Should extend period when selected period is too short")
        void should_extend_period_when_selected_period_is_too_short() {
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
        @DisplayName("Should return negative when no suitable offer exists within maximum period")
        void should_return_negative_when_no_suitable_offer_exists_within_maximum_period(){
            //given
            String lowModifierUser = "1234567890";
            UserCreditRegistry testRegistry = personalCode -> lowModifierUser.equals(personalCode) ? 10 : -1;
            LoanDecisionEngine loanEngine = new LoanDecisionEngine(testRegistry);
            LoanRequest request = buildLoanRequest("1234567890", "2500", 12);
            //when
            LoanOffer offer = loanEngine.decide(request);
            //then
            assertThat(offer.outcome()).isEqualTo(DecisionOutcome.NEGATIVE);
            assertThat(offer.amount()).isEqualTo(BigDecimal.ZERO);
        }
    }
}