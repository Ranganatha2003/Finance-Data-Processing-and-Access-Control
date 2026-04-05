package com.example.financebackend.dto.dashboard;

import java.util.List;

public record CategorySummaryResponse(
    List<CategoryTotalsItemResponse> items
) {}

