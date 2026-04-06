package com.example.financebackend.controller;

import com.example.financebackend.dto.auth.AuthResponse;
import com.example.financebackend.dto.auth.LoginRequest;
import com.example.financebackend.dto.auth.RegisterRequest;
import com.example.financebackend.exception.InvalidInputException;
import com.example.financebackend.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final ObjectMapper objectMapper;
  private final Validator validator;

  public AuthController(AuthService authService, ObjectMapper objectMapper, Validator validator) {
    this.authService = authService;
    this.objectMapper = objectMapper;
    this.validator = validator;
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody String rawBody) {
    RegisterRequest request = parseAndValidate(rawBody, RegisterRequest.class);
    AuthResponse response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody String rawBody) {
    LoginRequest request = parseAndValidate(rawBody, LoginRequest.class);
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  private <T> T parseAndValidate(String rawBody, Class<T> type) {
    if (rawBody == null || rawBody.isBlank()) {
      throw new InvalidInputException("Request body is required");
    }
    T request;
    try {
      request = objectMapper.readValue(rawBody, type);
    } catch (JsonProcessingException e) {
      throw new InvalidInputException("Body must be valid JSON with the required fields");
    }
    Set<ConstraintViolation<T>> violations = validator.validate(request);
    if (!violations.isEmpty()) {
      String message = violations.iterator().next().getMessage();
      throw new InvalidInputException(message);
    }
    return request;
  }
}

