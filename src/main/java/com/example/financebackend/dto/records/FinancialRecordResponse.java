package com.example.financebackend.dto.records;

import com.example.financebackend.enums.RecordType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FinancialRecordResponse(
    Long id,
    BigDecimal amount,
    RecordType type,
    String category,
    LocalDate date,
    String description,
    Long createdByUserId,
    LocalDateTime createdAt
) {}

