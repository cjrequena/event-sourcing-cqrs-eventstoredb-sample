package com.cjrequena.sample.exception.service;

import lombok.ToString;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@ToString
public abstract class ServiceException extends Exception {
  public ServiceException(Throwable ex) {
    super(ex);
  }

  public ServiceException(String message) {
    super(message);
  }

  public ServiceException(String message, Throwable ex) {
    super(message, ex);
  }

}
