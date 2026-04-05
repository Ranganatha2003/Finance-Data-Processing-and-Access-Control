package com.example.financebackend.controller;

import com.example.financebackend.dto.dashboard.CategorySummaryResponse;
import com.example.financebackend.dto.dashboard.DashboardSummaryResponse;
import com.example.financebackend.dto.dashboard.MonthlyTrendResponse;
import com.example.financebackend.dto.dashboard.RecentActivityResponse;
import com.example.financebackend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/summary")
  public ResponseEntity<DashboardSummaryResponse> summary() {
    return ResponseEntity.ok(dashboardService.getSummary());
  }

  @GetMapping("/category-summary")
  public ResponseEntity<CategorySummaryResponse> categorySummary() {
    return ResponseEntity.ok(dashboardService.getCategorySummary());
  }

  @GetMapping("/monthly-trend")
  public ResponseEntity<MonthlyTrendResponse> monthlyTrend() {
    return ResponseEntity.ok(dashboardService.getMonthlyTrend());
  }

  @GetMapping("/recent-activity")
  public ResponseEntity<RecentActivityResponse> recentActivity() {
    return ResponseEntity.ok(dashboardService.getRecentActivity());
  }
}

