package com.bartek.supportportal.exception.domain;

public class EmailExistException extends Exception {
  public EmailExistException(String message) {
    super(message);
  }
}
