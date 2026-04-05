package com.example.financebackend.controller;

import com.example.financebackend.dto.records.CreateFinancialRecordRequest;
import com.example.financebackend.dto.records.FinancialRecordResponse;
import com.example.financebackend.dto.records.UpdateFinancialRecordRequest;
import com.example.financebackend.enums.RecordType;
import com.example.financebackend.service.FinancialRecordService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/records")
public class FinancialRecordController {

  private final FinancialRecordService recordService;

  public FinancialRecordController(FinancialRecordService recordService) {
    this.recordService = recordService;
  }

  @PostMapping
  public ResponseEntity<FinancialRecordResponse> createRecord(
      @Valid @RequestBody CreateFinancialRecordRequest request
  ) {
    FinancialRecordResponse response = recordService.createRecord(request);
    return ResponseEntity.status(201).body(response);
  }

  @GetMapping
  public ResponseEntity<List<FinancialRecordResponse>> getRecords(
      @RequestParam(required = false) RecordType type,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
  ) {
    return ResponseEntity.ok(recordService.getRecords(type, category, startDate, endDate));
  }

  @GetMapping("/{id}")
  public ResponseEntity<FinancialRecordResponse> getRecord(@PathVariable Long id) {
    return ResponseEntity.ok(recordService.getRecordById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<FinancialRecordResponse> updateRecord(
      @PathVariable Long id,
      @Valid @RequestBody UpdateFinancialRecordRequest request
  ) {
    return ResponseEntity.ok(recordService.updateRecord(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
    recordService.deleteRecord(id);
    return ResponseEntity.noContent().build();
  }
}

