package com.example.financebackend.dto.dashboard;

import java.util.List;

public record RecentActivityResponse(
    List<RecentActivityItemResponse> items
) {}

