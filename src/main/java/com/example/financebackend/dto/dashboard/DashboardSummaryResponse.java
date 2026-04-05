package com.example.financebackend.dto.dashboard;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
    BigDecimal totalIncome,
    BigDecimal totalExpenses,
    BigDecimal netBalance
) {}

