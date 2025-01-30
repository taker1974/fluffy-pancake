package ru.hogwarts.school.dto;

import jakarta.annotation.Nullable;

public record ErrorResponse(int code, String message, @Nullable String details) {

    public ErrorResponse(int code, String message) {
        this(code, message, "");
    }
}
