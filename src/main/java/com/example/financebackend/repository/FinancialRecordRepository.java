package com.example.financebackend.repository;

import com.example.financebackend.entity.FinancialRecord;
import com.example.financebackend.enums.RecordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

  Optional<FinancialRecord> findById(Long id);

  List<FinancialRecord> findTop10ByOrderByCreatedAtDesc();

  List<FinancialRecord> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

  @Query("""
      SELECT r
      FROM FinancialRecord r
      WHERE (:type IS NULL OR r.type = :type)
        AND (:category IS NULL OR r.category = :category)
        AND (:startDate IS NULL OR r.date >= :startDate)
        AND (:endDate IS NULL OR r.date <= :endDate)
      ORDER BY r.date DESC, r.createdAt DESC
      """)
  List<FinancialRecord> findFiltered(
      @Param("type") RecordType type,
      @Param("category") String category,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );
}

