package ru.hogwarts.school.dto;

import jakarta.annotation.Nullable;

public record ErrorResponseDto(int code, String message, @Nullable String details) {

    public ErrorResponseDto(int code, String message) {
        this(code, message, "");
    }
}
