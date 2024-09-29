package com.alibou.book.exception;

public class TokenException extends RuntimeException {
  public void expiredTokenException(String message){
    super(message);
  }
}
