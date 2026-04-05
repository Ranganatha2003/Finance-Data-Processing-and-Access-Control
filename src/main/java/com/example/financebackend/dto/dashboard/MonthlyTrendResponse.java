package com.example.financebackend.dto.dashboard;

import java.util.List;

public record MonthlyTrendResponse(
    List<MonthlyTrendPointResponse> points
) {}

