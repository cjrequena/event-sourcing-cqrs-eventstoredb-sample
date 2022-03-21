package com.cjrequena.sample.command;

import com.cjrequena.sample.common.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
public enum ECommandType {

  CREATE_BANK_ACCOUNT_COMMAND(Constants.CREATE_BANK_ACCOUNT_COMMAND),
  DEPOSIT_BANK_ACCOUNT_COMMAND(Constants.DEPOSIT_BANK_ACCOUNT_COMMAND),
  WITHDRAW_BANK_ACCOUNT_COMMAND(Constants.WITHDRAW_BANK_ACCOUNT_COMMAND);

  @JsonValue
  @Getter
  private final String value;


  ECommandType(String value) {
    this.value = value;
  }

  @JsonCreator
  public static ECommandType parse(String value) {
    return Arrays.stream(ECommandType.values()).filter(e -> e.getValue().equals(value)).findFirst().orElse(null);
  }

}
