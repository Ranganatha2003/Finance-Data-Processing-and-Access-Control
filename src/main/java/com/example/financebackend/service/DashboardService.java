package com.example.financebackend.service;

import com.example.financebackend.dto.dashboard.*;
import com.example.financebackend.dto.records.FinancialRecordResponse;
import com.example.financebackend.entity.FinancialRecord;
import com.example.financebackend.enums.RecordType;
import com.example.financebackend.repository.FinancialRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

  private final FinancialRecordRepository recordRepository;

  public DashboardService(FinancialRecordRepository recordRepository) {
    this.recordRepository = recordRepository;
  }

  public DashboardSummaryResponse getSummary() {
    List<FinancialRecord> records = recordRepository.findAll();

    BigDecimal totalIncome = BigDecimal.ZERO;
    BigDecimal totalExpenses = BigDecimal.ZERO;

    for (FinancialRecord record : records) {
      if (record.getType() == RecordType.INCOME) {
        totalIncome = totalIncome.add(record.getAmount());
      } else if (record.getType() == RecordType.EXPENSE) {
        totalExpenses = totalExpenses.add(record.getAmount());
      }
    }

    BigDecimal netBalance = totalIncome.subtract(totalExpenses);
    return new DashboardSummaryResponse(totalIncome, totalExpenses, netBalance);
  }

  public CategorySummaryResponse getCategorySummary() {
    List<FinancialRecord> records = recordRepository.findAll();

    Map<String, BigDecimal> incomeByCategory = new HashMap<>();
    Map<String, BigDecimal> expenseByCategory = new HashMap<>();

    for (FinancialRecord record : records) {
      String category = record.getCategory();

      if (record.getType() == RecordType.INCOME) {
        incomeByCategory.put(category, incomeByCategory.getOrDefault(category, BigDecimal.ZERO).add(record.getAmount()));
      } else if (record.getType() == RecordType.EXPENSE) {
        expenseByCategory.put(category, expenseByCategory.getOrDefault(category, BigDecimal.ZERO).add(record.getAmount()));
      }
    }

    // Deterministic order for predictable output.
    List<String> categories = incomeByCategory.keySet().stream()
        .sorted()
        .toList();
    for (String category : expenseByCategory.keySet()) {
      if (!incomeByCategory.containsKey(category)) {
        categories.add(category);
      }
    }

    List<CategoryTotalsItemResponse> items = categories.stream()
        .map(category -> {
          BigDecimal income = incomeByCategory.getOrDefault(category, BigDecimal.ZERO);
          BigDecimal expenses = expenseByCategory.getOrDefault(category, BigDecimal.ZERO);
          return new CategoryTotalsItemResponse(category, income, expenses);
        })
        .toList();

    return new CategorySummaryResponse(items);
  }

  public MonthlyTrendResponse getMonthlyTrend() {
    // Simplification:
    // Return the last 6 months (including current month).
    LocalDate today = LocalDate.now();
    YearMonth endMonth = YearMonth.from(today);
    YearMonth startMonth = endMonth.minusMonths(5);

    LocalDate startDate = startMonth.atDay(1);
    LocalDate endDate = endMonth.atEndOfMonth();

    List<FinancialRecord> records = recordRepository.findFiltered(null, null, startDate, endDate);

    Map<YearMonth, BigDecimal> incomeByMonth = new HashMap<>();
    Map<YearMonth, BigDecimal> expenseByMonth = new HashMap<>();

    for (FinancialRecord record : records) {
      YearMonth ym = YearMonth.from(record.getDate());

      if (record.getType() == RecordType.INCOME) {
        incomeByMonth.put(ym, incomeByMonth.getOrDefault(ym, BigDecimal.ZERO).add(record.getAmount()));
      } else if (record.getType() == RecordType.EXPENSE) {
        expenseByMonth.put(ym, expenseByMonth.getOrDefault(ym, BigDecimal.ZERO).add(record.getAmount()));
      }
    }

    // Build points for each month even if there were no records.
    int monthsCount = 6;
    MonthlyTrendPointResponse[] points = new MonthlyTrendPointResponse[monthsCount];
    for (int i = 0; i < monthsCount; i++) {
      YearMonth ym = startMonth.plusMonths(i);
      BigDecimal income = incomeByMonth.getOrDefault(ym, BigDecimal.ZERO);
      BigDecimal expenses = expenseByMonth.getOrDefault(ym, BigDecimal.ZERO);
      BigDecimal net = income.subtract(expenses);

      points[i] = new MonthlyTrendPointResponse(ym.toString(), income, expenses, net);
    }

    return new MonthlyTrendResponse(List.of(points));
  }

  public RecentActivityResponse getRecentActivity() {
    List<FinancialRecord> recent = recordRepository.findTop10ByOrderByCreatedAtDesc();
    List<RecentActivityItemResponse> items = recent.stream()
        .map(this::toRecentItem)
        .toList();
    return new RecentActivityResponse(items);
  }

  private RecentActivityItemResponse toRecentItem(FinancialRecord record) {
    Long createdByUserId = record.getCreatedBy() != null ? record.getCreatedBy().getId() : null;
    return new RecentActivityItemResponse(
        record.getId(),
        record.getAmount(),
        record.getType(),
        record.getCategory(),
        record.getDate(),
        record.getDescription(),
        createdByUserId,
        record.getCreatedAt()
    );
  }
}

