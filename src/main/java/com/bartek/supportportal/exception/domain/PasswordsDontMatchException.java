package com.bartek.supportportal.exception.domain;

public class PasswordsDontMatchException extends Exception{
    public PasswordsDontMatchException(String message) {
        super(message);
    }
}
