package com.cjrequena.sample.event;

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
public enum EEventSchemaType {

  BANK_ACCOUNT_CREATED_EVENT_SCHEMA_V1(Constants.BANK_ACCOUNT_CREATED_EVENT_SCHEMA_V1),
  BANK_ACCOUNT_DEPOSITED_EVENT_SCHEMA_V1(Constants.BANK_ACCOUNT_DEPOSITED_EVENT_SCHEMA_V1),
  BANK_ACCOUNT_WITHDRAWN_EVENT_SCHEMA_V1(Constants.BANK_ACCOUNT_WITHDRAWN_EVENT_SCHEMA_V1);

  @JsonValue
  @Getter
  private final String value;

  EEventSchemaType(String value) {
    this.value = value;
  }

  @JsonCreator
  public static EEventSchemaType parse(String value) {
    return Arrays.stream(EEventSchemaType.values()).filter(e -> e.getValue().equals(value)).findFirst().orElse(null);
  }

}

