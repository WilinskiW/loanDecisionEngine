package com.task.decisionengine.infrastructure;

import com.task.decisionengine.domain.LoanOffer;
import com.task.decisionengine.domain.LoanRequest;
import com.task.decisionengine.infrastructure.dto.LoanOfferResponseDto;
import com.task.decisionengine.infrastructure.dto.LoanRequestDto;

public class LoanMapper {
    public static LoanRequest mapToDomainLoanRequest(LoanRequestDto dto) {
        return LoanRequest.builder()
                .personalCode(dto.personalCode())
                .amount(dto.amount())
                .period(dto.period())
                .build();
    }

    public static LoanOfferResponseDto mapToDtoLoanResponse(LoanOffer offer) {
        return new LoanOfferResponseDto(offer.outcome(), offer.amount());
    }
}
