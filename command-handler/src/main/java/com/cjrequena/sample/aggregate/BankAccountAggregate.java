package com.cjrequena.sample.aggregate;

import com.cjrequena.sample.dto.BankAccountDTO;
import com.cjrequena.sample.event.BankAccountCratedEvent;
import com.cjrequena.sample.event.BankAccountDepositedEvent;
import com.cjrequena.sample.event.BankAccountWithdrawnEvent;
import com.cjrequena.sample.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import static com.cjrequena.sample.common.Constants.BANK_ACCOUNT_AGGREGATE_NAME;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Slf4j
public class BankAccountAggregate extends Aggregate {

  private BankAccountDTO bankAccountDTO;

  public BankAccountAggregate(UUID aggregateId, List<Event> events) {
    super(aggregateId, BANK_ACCOUNT_AGGREGATE_NAME, events);
  }

  @Override
  public void replayEvents(List<Event> events) {
    events.forEach(
      event -> {
        switch (event.getType()) {
          case BANK_ACCOUNT_CREATED_EVENT_V1:
            this.apply((BankAccountCratedEvent) event);
            break;
          case BANK_ACCOUNT_DEPOSITED_EVENT_V1:
            this.apply((BankAccountDepositedEvent) event);
            break;
          case BANK_ACCOUNT_WITHDRAWN_EVENT_V1:
            this.apply((BankAccountWithdrawnEvent) event);
            break;
        }
        version = event.getVersion();
      });
  }

  public void apply(BankAccountCratedEvent event) {
    this.bankAccountDTO = new BankAccountDTO();
    this.bankAccountDTO.setId(event.getData().getId());
    this.bankAccountDTO.setOwner(event.getData().getOwner());
    this.bankAccountDTO.setOwner(event.getData().getOwner());
    this.bankAccountDTO.setBalance(event.getData().getBalance());
  }

  public void apply(BankAccountDepositedEvent event) {
    this.bankAccountDTO.setBalance(this.bankAccountDTO.getBalance().add(event.getData().getAmount()));
  }

  public void apply(BankAccountWithdrawnEvent event) {
    this.bankAccountDTO.setBalance(this.bankAccountDTO.getBalance().subtract(event.getData().getAmount()));
  }
}
