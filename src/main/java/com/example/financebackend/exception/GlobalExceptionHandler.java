package com.example.financebackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ApiErrorResponse> handleUnsupportedMediaType(
      HttpMediaTypeNotSupportedException ex,
      HttpServletRequest request
  ) {
    String message =
        "Unsupported Content-Type for this endpoint. Send JSON in the body (Postman: Body → raw → JSON, or text/plain with JSON text).";
    log.warn("Unsupported media type on {}: {}", request.getRequestURI(), ex.getContentType());
    return toResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message, request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGeneric(
      Exception ex,
      HttpServletRequest request
  ) {
    // Log the real cause in the server console (check the terminal running Spring Boot).
    log.error("Unexpected error on {}: {}", request.getRequestURI(), ex.toString(), ex);
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

