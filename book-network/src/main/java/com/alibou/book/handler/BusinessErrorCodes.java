package com.alibou.book.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum BusinessErrorCodes {

  NO_CODE(0,NOT_IMPLEMENTED,"No codigo"),
  INCORRECT_CURRENT_PASSWORD(300,BAD_REQUEST,"Contraseña incorrecta"),
  NEW_PASSWORD_DOES_NOT_MATCH (301,BAD_REQUEST,"La nueva contraseña no coincide"),
  ACCOUNT_LOCKED(302,FORBIDDEN,"Cuenta de usuario bloqueada"),
  ACCOUNT_DISABLED(303,FORBIDDEN,"Cuenta de usuario desahabilitada"),
  BAD_CREDENTIALS(304,FORBIDDEN,"Usuario y/o contraseña incorrecta"),
  ;

  private final int code;
  private final String description;
  private final HttpStatus httpStatus;

  BusinessErrorCodes(int code, HttpStatus httpStatus,String description) {
    this.code = code;
    this.description = description;
    this.httpStatus = httpStatus;
  }
}
