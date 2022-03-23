package com.cjrequena.sample.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@JsonPropertyOrder(value = {
  "accountId",
  "amount"
})
@Schema
@Data
public class WithdrawBankAccountDTO {

  @JsonProperty(value = "account_id")
  private UUID accountId;

  @JsonProperty(value = "amount")
  private BigDecimal amount;

}
