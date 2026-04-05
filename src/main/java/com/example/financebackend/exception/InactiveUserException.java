package com.example.financebackend.exception;

public class InactiveUserException extends RuntimeException {

  public InactiveUserException(String message) {
    super(message);
  }
}

