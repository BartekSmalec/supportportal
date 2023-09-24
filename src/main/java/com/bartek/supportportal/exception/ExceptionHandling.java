package com.bartek.supportportal.exception;

import static org.springframework.http.HttpStatus.*;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.bartek.supportportal.domain.HttpResponse;
import com.bartek.supportportal.exception.domain.*;
import java.io.IOException;
import jakarta.persistence.NoResultException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandling {
  private static final String ACCOUNT_LOCKED =
      "Your account has been locked. Please contact administration";
  private static final String METHOD_IS_NOT_ALLOWED =
      "This request method is not allowed on this endpoint. Please send a '%s' request";
  private static final String INTERNAL_SERVER_ERROR_MSG =
      "An error occurred while processing the request";
  private static final String INCORRECT_CREDENTIALS =
      "Username / password incorrect. Please try again";
  private static final String ACCOUNT_DISABLED =
      "Your account has been disabled. If this is an error, please contact administration";
  private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
  private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
  public static final String ERROR_PATH = "/error";

  private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
    return new ResponseEntity<>(
        new HttpResponse(
            httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message),
        httpStatus);
  }

  @ExceptionHandler(JWTDecodeException.class)
  public ResponseEntity<HttpResponse> jwtDecodeException() {
    return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  ResponseEntity<HttpResponse> handleConstraintViolationException(ConstraintViolationException e) {
    return createHttpResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(NotAnImageFileException.class)
  public ResponseEntity<HttpResponse> notAnImageFileException(NotAnImageFileException exception) {
    return createHttpResponse(BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<HttpResponse> accountDisabledException() {
    return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<HttpResponse> badCredentialException() {
    return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<HttpResponse> accessDeniedException() {
    return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
  }

  @ExceptionHandler(LockedException.class)
  public ResponseEntity<HttpResponse> lockedException() {
    return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
  }

  @ExceptionHandler(TokenExpiredException.class)
  public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException e) {
    return createHttpResponse(UNAUTHORIZED, e.getMessage().toUpperCase());
  }

  @ExceptionHandler(EmailExistException.class)
  public ResponseEntity<HttpResponse> emailExistException(EmailExistException e) {
    return createHttpResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(UsernameExistException.class)
  public ResponseEntity<HttpResponse> usernameExistException(UsernameExistException e) {
    return createHttpResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(EmailNotFoundException.class)
  public ResponseEntity<HttpResponse> emailNotFountException(EmailNotFoundException e) {
    return createHttpResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException e) {
    return createHttpResponse(BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<HttpResponse> methodNotSupportedException() {
    return createHttpResponse(METHOD_NOT_ALLOWED, METHOD_IS_NOT_ALLOWED);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<HttpResponse> internalServerErrorException(Exception e) {
    log.error(e.getMessage());
    return createHttpResponse(INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ExceptionHandler(NoResultException.class)
  public ResponseEntity<HttpResponse> notFoundException(NoResultException e) {
    log.error(e.getMessage());
    return createHttpResponse(NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<HttpResponse> ioException(IOException e) {
    log.error(e.getMessage());
    return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
  }
}
