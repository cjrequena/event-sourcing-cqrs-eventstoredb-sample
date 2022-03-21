package com.cjrequena.sample.exception.controller;

import org.springframework.http.HttpStatus;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class BadFiltersControllerException extends ControllerException {
  public BadFiltersControllerException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }
}
