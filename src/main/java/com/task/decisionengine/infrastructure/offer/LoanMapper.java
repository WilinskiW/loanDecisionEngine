package com.task.decisionengine.infrastructure.offer;

import com.task.decisionengine.domain.LoanOffer;
import com.task.decisionengine.domain.LoanRequest;
import com.task.decisionengine.infrastructure.dto.LoanOfferResponseDto;
import com.task.decisionengine.infrastructure.dto.LoanRequestDto;

public final class LoanMapper {
    private LoanMapper() {
    }

    public static LoanRequest toDomain(LoanRequestDto dto) {
        return LoanRequest.builder()
                .personalCode(dto.personalCode())
                .amount(dto.amount())
                .period(dto.period())
                .build();
    }

    public static LoanOfferResponseDto toResponseDto(LoanOffer offer) {
        return new LoanOfferResponseDto(offer.outcome(), offer.amount());
    }
}
