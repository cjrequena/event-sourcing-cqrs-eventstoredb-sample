package com.cjrequena.sample.event;

import com.cjrequena.sample.common.Constants;
import com.cjrequena.sample.dto.DepositBankAccountDTO;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.MediaType;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class BankAccountDepositedEvent extends Event<DepositBankAccountDTO> implements Serializable {

  @Builder
  public BankAccountDepositedEvent(
    UUID id,
    String source,
    String dataContentType,
    String subject,
    OffsetDateTime time,
    DepositBankAccountDTO data,
    String dataBase64,
    UUID aggregateId,
    Long version) {
    super(
      Optional.ofNullable(id).orElse(UUID.randomUUID()),
      Optional.ofNullable(source).orElse(Constants.CLOUD_EVENTS_SOURCE),
      EEventType.BANK_ACCOUNT_DEPOSITED_EVENT_V1,
      Optional.ofNullable(dataContentType).orElse(MediaType.APPLICATION_JSON_VALUE),
      subject,
      Optional.ofNullable(time).orElse(OffsetDateTime.now()),
      data,
      dataBase64,
      ESchemaType.BANK_ACCOUNT_DEPOSITED_EVENT_SCHEMA_V1,
      aggregateId,
      version);
  }
}
