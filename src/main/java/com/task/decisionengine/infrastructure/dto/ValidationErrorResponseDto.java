package com.task.decisionengine.infrastructure.dto;

public record ValidationErrorResponseDto(
        int status,
        String message
) {
}
