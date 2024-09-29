package com.alibou.book.exception;

public class InvalidTokenException extends RuntimeException {
  public InvalidTokenException(String message) {
    super(message);
  }
}
