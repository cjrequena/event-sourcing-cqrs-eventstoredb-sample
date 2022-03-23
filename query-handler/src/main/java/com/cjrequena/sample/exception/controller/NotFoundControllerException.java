package com.cjrequena.sample.exception.controller;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class NotFoundControllerException extends ControllerException {
  public NotFoundControllerException() {
    super(HttpStatus.NOT_FOUND);
  }

  public NotFoundControllerException(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }
}
