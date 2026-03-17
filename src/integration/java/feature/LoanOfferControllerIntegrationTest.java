package feature;

import com.task.decisionengine.DecisionEngineApplication;
import com.task.decisionengine.domain.LoanRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DecisionEngineApplication.class)
@AutoConfigureRestTestClient
public class LoanOfferControllerIntegrationTest {
    @Autowired
    private RestTestClient restTestClient;

    @Test
    @DisplayName("Should return maximum approved amount for given user and loan amount")
    void should_return_maximum_approved_amount() {
        var request = new LoanRequest("49002010987", new BigDecimal("4000"), 12);

        restTestClient.post()
                .uri("/api/offer")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.outcome").isEqualTo("POSITIVE")
                .jsonPath("$.amount").isEqualTo(3600);
    }

    @Test
    @DisplayName("Should return negative offer when user is not found in registry")
    void should_return_negative_offer_when_user_is_not_found_in_registry() {
        var request = new LoanRequest("72375010969", new BigDecimal("2500"), 30);

        restTestClient.post()
                .uri("/api/offer")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.outcome").isEqualTo("NEGATIVE")
                .jsonPath("$.amount").isEqualTo(0);
    }

    @Test
    @DisplayName("Should return negative offer when user has debt")
    void should_return_negative_offer_when_user_has_debt() {
        var request = new LoanRequest("49002010965", new BigDecimal("5000"), 12);

        restTestClient.post()
                .uri("/api/offer")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.outcome").isEqualTo("NEGATIVE")
                .jsonPath("$.amount").isEqualTo(0);
    }

    @Test
    @DisplayName("Should return error when validation fails")
    void should_return_error_when_validation_fails() {
        var aboveMaximumAmountRequest = new LoanRequest("49002010987", new BigDecimal("10001"), 12);
        makeValidationPostRequest(aboveMaximumAmountRequest, "Amount must be at most 10000");

        var belowMinimumAmountRequest = new LoanRequest("49002010987", new BigDecimal("1999"), 12);
        makeValidationPostRequest(belowMinimumAmountRequest, "Amount must be at least 2000");

        var belowMinimumPeriodRequest = new LoanRequest("49002010987", new BigDecimal("4000"), 11);
        makeValidationPostRequest(belowMinimumPeriodRequest, "Period must be at least 12");

        var aboveMaximumPeriodRequest = new LoanRequest("49002010987", new BigDecimal("4000"), 61);
        makeValidationPostRequest(aboveMaximumPeriodRequest, "Period must be at most 60");

        var personalCodeWithLetters = new LoanRequest("abcdeftgfgf", new BigDecimal("4000"), 12);
        makeValidationPostRequest(personalCodeWithLetters, "Personal code must contain only numbers");

        var shortPersonalCode = new LoanRequest("12345", new BigDecimal("4000"), 12);
        makeValidationPostRequest(shortPersonalCode, "Personal code must be exactly 11 characters long");
    }

    private void makeValidationPostRequest(LoanRequest body, String errorMessage){
        restTestClient.post()
                .uri("/api/offer")
                .body(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(errorMessage)
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    @DisplayName("Should return positive offer by extending period internally when requested period is too short")
    void should_return_positive_by_extending_period() {
        var request = new LoanRequest("49002010976", new BigDecimal("2000"), 12);

        restTestClient.post()
                .uri("/api/offer")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.outcome").isEqualTo("POSITIVE")
                .jsonPath("$.amount").isEqualTo(2000);
    }

    @Test
    @DisplayName("Should return reduced amount based on credit modifier and period")
    void should_return_reduced_amount() {
        var request = new LoanRequest("49002010976", new BigDecimal("4000"), 25);

        restTestClient.post()
                .uri("/api/offer")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.outcome").isEqualTo("POSITIVE")
                .jsonPath("$.amount").isEqualTo(2500);
    }

    @Test
    @DisplayName("Should strictly return only outcome and amount fields")
    void should_return_only_required_fields() {
        var request = new LoanRequest("49002010987", new BigDecimal("4000"), 12);

        restTestClient.post()
                .uri("/api/offer")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.outcome").exists()
                .jsonPath("$.amount").exists()
                .jsonPath("$.period").doesNotExist();
    }
}
