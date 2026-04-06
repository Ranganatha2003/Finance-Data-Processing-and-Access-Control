package com.example.financebackend.controller;

import com.example.financebackend.dto.users.UpdateUserRoleRequest;
import com.example.financebackend.dto.users.UpdateUserStatusRequest;
import com.example.financebackend.dto.users.UserResponse;
import com.example.financebackend.exception.ApiErrorResponse;
import com.example.financebackend.exception.InvalidInputException;
import com.example.financebackend.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final ObjectMapper objectMapper;
  private final Validator validator;

  public UserController(UserService userService, ObjectMapper objectMapper, Validator validator) {
    this.userService = userService;
    this.objectMapper = objectMapper;
    this.validator = validator;
  }

  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @GetMapping("/{id}/role")
  public ResponseEntity<ApiErrorResponse> updateUserRoleWrongMethod(HttpServletRequest request) {
    ApiErrorResponse body = ApiErrorResponse.of(
        HttpStatus.METHOD_NOT_ALLOWED.value(),
        "Method Not Allowed",
        "Use PUT (not GET) with a JSON body, for example: {\"role\":\"ANALYST\"}",
        request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .header(HttpHeaders.ALLOW, "PUT")
        .body(body);
  }

  @PutMapping("/{id}/role")
  public ResponseEntity<UserResponse> updateUserRole(
      @PathVariable Long id,
      @RequestBody String rawBody
  ) {
    UpdateUserRoleRequest request = parseAndValidate(rawBody, UpdateUserRoleRequest.class);
    return ResponseEntity.ok(userService.updateUserRole(id, request));
  }

  @GetMapping("/{id}/status")
  public ResponseEntity<ApiErrorResponse> updateUserStatusWrongMethod(HttpServletRequest request) {
    ApiErrorResponse body = ApiErrorResponse.of(
        HttpStatus.METHOD_NOT_ALLOWED.value(),
        "Method Not Allowed",
        "Use PUT (not GET) with a JSON body, for example: {\"status\":\"ACTIVE\"}",
        request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .header(HttpHeaders.ALLOW, "PUT")
        .body(body);
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<UserResponse> updateUserStatus(
      @PathVariable Long id,
      @RequestBody String rawBody
  ) {
    UpdateUserStatusRequest request = parseAndValidate(rawBody, UpdateUserStatusRequest.class);
    return ResponseEntity.ok(userService.updateUserStatus(id, request));
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

