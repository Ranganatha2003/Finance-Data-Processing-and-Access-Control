package com.example.financebackend.exception;

public class DuplicateEmailException extends RuntimeException {

  public DuplicateEmailException(String message) {
    super(message);
  }
}

