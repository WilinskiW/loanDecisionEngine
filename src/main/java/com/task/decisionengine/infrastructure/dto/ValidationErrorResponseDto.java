package com.task.decisionengine.infrastructure.dto;

public record ValidationErrorResponseDto(
        int code,
        String message
) {
}
