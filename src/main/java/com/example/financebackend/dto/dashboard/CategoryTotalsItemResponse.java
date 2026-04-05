package com.example.financebackend.dto.dashboard;

import java.math.BigDecimal;

public record CategoryTotalsItemResponse(
    String category,
    BigDecimal totalIncome,
    BigDecimal totalExpenses
) {}

