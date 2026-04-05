package com.example.financebackend.config;

import com.example.financebackend.entity.FinancialRecord;
import com.example.financebackend.entity.User;
import com.example.financebackend.enums.RecordType;
import com.example.financebackend.enums.Role;
import com.example.financebackend.enums.UserStatus;
import com.example.financebackend.repository.FinancialRecordRepository;
import com.example.financebackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final FinancialRecordRepository recordRepository;
  private final PasswordEncoder passwordEncoder;

  public DataSeeder(
      UserRepository userRepository,
      FinancialRecordRepository recordRepository,
      PasswordEncoder passwordEncoder
  ) {
    this.userRepository = userRepository;
    this.recordRepository = recordRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    // Avoid inserting duplicates on every app restart.
    if (userRepository.existsByEmail("admin@demo.com")) {
      return;
    }

    User admin = new User();
    admin.setName("Admin User");
    admin.setEmail("admin@demo.com");
    admin.setPassword(passwordEncoder.encode("password123"));
    admin.setRole(Role.ADMIN);
    admin.setStatus(UserStatus.ACTIVE);

    User analyst = new User();
    analyst.setName("Analyst User");
    analyst.setEmail("analyst@demo.com");
    analyst.setPassword(passwordEncoder.encode("password123"));
    analyst.setRole(Role.ANALYST);
    analyst.setStatus(UserStatus.ACTIVE);

    User viewer = new User();
    viewer.setName("Viewer User");
    viewer.setEmail("viewer@demo.com");
    viewer.setPassword(passwordEncoder.encode("password123"));
    viewer.setRole(Role.VIEWER);
    viewer.setStatus(UserStatus.ACTIVE);

    userRepository.saveAll(List.of(admin, analyst, viewer));

    LocalDate now = LocalDate.now();

    List<FinancialRecord> records = List.of(
        // Income
        buildRecord(RecordType.INCOME, "Salary", now.minusMonths(2), new BigDecimal("5000.00"), "Monthly salary", admin),
        buildRecord(RecordType.INCOME, "Freelance", now.minusMonths(1).minusDays(3), new BigDecimal("800.00"), "Project payout", analyst),
        buildRecord(RecordType.INCOME, "Salary", now.minusMonths(0).minusDays(5), new BigDecimal("5000.00"), "Monthly salary", viewer),

        // Expenses
        buildRecord(RecordType.EXPENSE, "Food", now.minusMonths(2).minusDays(10), new BigDecimal("420.50"), "Groceries and snacks", admin),
        buildRecord(RecordType.EXPENSE, "Transport", now.minusMonths(1).minusDays(6), new BigDecimal("160.00"), "Fuel and metro", analyst),
        buildRecord(RecordType.EXPENSE, "Bills", now.minusMonths(0).minusDays(2), new BigDecimal("230.75"), "Electricity and internet", viewer)
    );

    recordRepository.saveAll(records);
  }

  private FinancialRecord buildRecord(
      RecordType type,
      String category,
      LocalDate date,
      BigDecimal amount,
      String description,
      User createdBy
  ) {
    FinancialRecord record = new FinancialRecord();
    record.setType(type);
    record.setCategory(category);
    record.setDate(date);
    record.setAmount(amount);
    record.setDescription(description);
    record.setCreatedBy(createdBy);
    return record;
  }
}

