package com.example.financebackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFound(
      ResourceNotFoundException ex,
      HttpServletRequest request
  ) {
    return toResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ApiErrorResponse> handleDuplicateEmail(
      DuplicateEmailException ex,
      HttpServletRequest request
  ) {
    return toResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(InactiveUserException.class)
  public ResponseEntity<ApiErrorResponse> handleInactiveUser(
      InactiveUserException ex,
      HttpServletRequest request
  ) {
    // Inactive accounts behave like unauthorized access.
    return toResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(InvalidInputException.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidInput(
      InvalidInputException ex,
      HttpServletRequest request
  ) {
    return toResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler({
      BadCredentialsException.class,
      UsernameNotFoundException.class
  })
  public ResponseEntity<ApiErrorResponse> handleAuthFailure(
      RuntimeException ex,
      HttpServletRequest request
  ) {
    return toResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiErrorResponse> handleForbidden(
      AccessDeniedException ex,
      HttpServletRequest request
  ) {
    return toResponse(HttpStatus.FORBIDDEN, "Forbidden", request.getRequestURI());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex,
      HttpServletRequest request
  ) {
    List<FieldValidationError> fieldErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(err -> new FieldValidationError(err.getField(), err.getDefaultMessage()))
        .toList();

    ApiErrorResponse response = new ApiErrorResponse(
        java.time.Instant.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Bad Request",
        "Validation failed",
        request.getRequestURI(),
        fieldErrors
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex,
      HttpServletRequest request
  ) {
    return toResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGeneric(
      Exception ex,
      HttpServletRequest request
  ) {
    // Avoid leaking stack traces. Keep message simple for assignment.
    return toResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request.getRequestURI());
  }

  private ResponseEntity<ApiErrorResponse> toResponse(HttpStatus status, String message, String path) {
    ApiErrorResponse response = ApiErrorResponse.of(
        status.value(),
        status.getReasonPhrase(),
        message,
        path
    );
    return ResponseEntity.status(status).body(response);
  }
}

