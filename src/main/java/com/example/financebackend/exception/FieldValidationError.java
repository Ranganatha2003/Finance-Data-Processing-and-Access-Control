package com.example.financebackend.exception;

public record FieldValidationError(
    String field,
    String message
) {}

