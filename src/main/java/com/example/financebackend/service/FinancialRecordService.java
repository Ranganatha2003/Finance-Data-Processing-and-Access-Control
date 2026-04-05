package com.example.financebackend.service;

import com.example.financebackend.dto.records.CreateFinancialRecordRequest;
import com.example.financebackend.dto.records.FinancialRecordResponse;
import com.example.financebackend.dto.records.UpdateFinancialRecordRequest;
import com.example.financebackend.entity.FinancialRecord;
import com.example.financebackend.entity.User;
import com.example.financebackend.enums.RecordType;
import com.example.financebackend.exception.InvalidInputException;
import com.example.financebackend.exception.ResourceNotFoundException;
import com.example.financebackend.repository.FinancialRecordRepository;
import com.example.financebackend.repository.UserRepository;
import com.example.financebackend.security.FinanceUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialRecordService {

  private final FinancialRecordRepository recordRepository;
  private final UserRepository userRepository;

  public FinancialRecordService(FinancialRecordRepository recordRepository, UserRepository userRepository) {
    this.recordRepository = recordRepository;
    this.userRepository = userRepository;
  }

  public FinancialRecordResponse createRecord(CreateFinancialRecordRequest request) {
    User currentUser = getCurrentUser();

    FinancialRecord record = new FinancialRecord();
    record.setAmount(request.amount());
    record.setType(request.type());
    record.setCategory(request.category());
    record.setDate(request.date());
    record.setDescription(request.description());
    record.setCreatedBy(currentUser);

    FinancialRecord saved = recordRepository.save(record);
    return toResponse(saved);
  }

  public List<FinancialRecordResponse> getRecords(RecordType type, String category, LocalDate startDate, LocalDate endDate) {
    // Repository method handles nulls for optional filters.
    return recordRepository.findFiltered(type, category, startDate, endDate)
        .stream()
        .map(this::toResponse)
        .toList();
  }

  public FinancialRecordResponse getRecordById(Long id) {
    FinancialRecord record = recordRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + id));

    return toResponse(record);
  }

  public FinancialRecordResponse updateRecord(Long id, UpdateFinancialRecordRequest request) {
    FinancialRecord record = recordRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + id));

    record.setAmount(request.amount());
    record.setType(request.type());
    record.setCategory(request.category());
    record.setDate(request.date());
    record.setDescription(request.description());

    FinancialRecord saved = recordRepository.save(record);
    return toResponse(saved);
  }

  public void deleteRecord(Long id) {
    if (!recordRepository.existsById(id)) {
      throw new ResourceNotFoundException("Record not found: " + id);
    }
    recordRepository.deleteById(id);
  }

  public List<FinancialRecord> getRecentRecords() {
    return recordRepository.findTop10ByOrderByCreatedAtDesc();
  }

  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication.getPrincipal() == null) {
      throw new InvalidInputException("User is not authenticated");
    }

    Object principal = authentication.getPrincipal();
    if (!(principal instanceof FinanceUserDetails details)) {
      throw new InvalidInputException("Unexpected authentication principal");
    }

    Long userId = details.getUserId();
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("Current user not found: " + userId));
  }

  private FinancialRecordResponse toResponse(FinancialRecord record) {
    Long createdByUserId = record.getCreatedBy() != null ? record.getCreatedBy().getId() : null;
    return new FinancialRecordResponse(
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

