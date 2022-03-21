package com.cjrequena.sample.command;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 */
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
public abstract class Command<T> {

  @NotNull
  protected UUID aggregateId;

  @NotNull
  protected ECommandType type;

  @NotNull
  protected T data;

  @NotNull
  protected Long version;

}
