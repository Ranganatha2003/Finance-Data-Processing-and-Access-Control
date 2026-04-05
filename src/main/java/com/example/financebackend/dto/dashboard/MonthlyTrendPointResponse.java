package com.example.financebackend.dto.dashboard;

import java.math.BigDecimal;

public record MonthlyTrendPointResponse(
    String month, // format: yyyy-MM
    BigDecimal totalIncome,
    BigDecimal totalExpenses,
    BigDecimal netBalance
) {}

