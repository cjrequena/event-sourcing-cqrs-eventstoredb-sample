package com.cjrequena.sample.event;

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
public enum EEventStreams {

  BANK_ACCOUNTS("bank_accounts");

  @JsonValue
  @Getter
  private final String value;

  @Getter
  private final String prefix;

  @Getter
  private final String categorySelector;

  @Getter
  private final String eventTypeSelector;

  EEventStreams(String value) {
    this.value = value;
    this.prefix = value + "-";
    this.categorySelector = "$ce-" + value;
    this.eventTypeSelector = "$et-" + value;
  }

  @JsonCreator
  public static EEventStreams parse(String name) {
    return Arrays.stream(EEventStreams.values()).filter(e -> e.getValue().equals(name)).findFirst().orElse(null);
  }

}
