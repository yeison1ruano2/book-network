package com.alibou.book.auth;

import lombok.Getter;

@Getter
public enum EMailTemplateName {

  ACTIVATE_ACCOUNT("activate_account");

  private final String name;

  EMailTemplateName(String name){
    this.name = name;
  }

}
