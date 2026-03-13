package com.task.decisionengine.infrastructure;

import com.task.decisionengine.domain.LoanDecisionEngine;
import com.task.decisionengine.domain.LoanOffer;
import com.task.decisionengine.infrastructure.dto.LoanOfferResponseDto;
import com.task.decisionengine.infrastructure.dto.LoanRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/offer")
public class LoanOfferController {
    private final LoanDecisionEngine engine;

    public LoanOfferController(LoanDecisionEngine engine) {
        this.engine = engine;
    }

    @PostMapping
    public ResponseEntity<LoanOfferResponseDto> getOffer(@RequestBody @Valid LoanRequestDto request) {
        var mappedRequest = LoanMapper.mapToDomainLoanRequest(request);
        LoanOffer offer = engine.decide(mappedRequest);
        return ResponseEntity.ok(LoanMapper.mapToDtoLoanResponse(offer));
    }
}
