package com.cjrequena.sample.exception.controller;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class NotAcceptableControllerException extends ControllerException {
  public NotAcceptableControllerException() {
    super(HttpStatus.NOT_ACCEPTABLE);
  }
}
