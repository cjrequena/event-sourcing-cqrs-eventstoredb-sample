package com.cjrequena.sample.exception.service;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public class OptimisticConcurrencyServiceException extends ServiceException {
  public OptimisticConcurrencyServiceException(String message) {
    super(message);
  }
}
