package com.cjrequena.sample.service;

import com.cjrequena.sample.aggregate.BankAccountAggregate;
import com.cjrequena.sample.command.Command;
import com.cjrequena.sample.command.CreateBankAccountCommand;
import com.cjrequena.sample.command.DepositBankAccountCommand;
import com.cjrequena.sample.command.WithdrawBankAccountCommand;
import com.cjrequena.sample.dto.BankAccountDTO;
import com.cjrequena.sample.dto.DepositBankAccountDTO;
import com.cjrequena.sample.dto.WithdrawBankAccountDTO;
import com.cjrequena.sample.event.BankAccountCratedEvent;
import com.cjrequena.sample.event.BankAccountDepositedEvent;
import com.cjrequena.sample.event.BankAccountWithdrawnEvent;
import com.cjrequena.sample.event.Event;
import com.cjrequena.sample.exception.service.AggregateNotFoundServiceException;
import com.cjrequena.sample.exception.service.BankAccountServiceException;
import com.cjrequena.sample.exception.service.DuplicatedAggregateServiceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.cjrequena.sample.mapper.BankAccountMapper;
import com.eventstore.dbclient.RecordedEvent;
import com.eventstore.dbclient.ResolvedEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cjrequena.sample.common.Constants.*;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BankAccountCommandService {

  private final EventStoreService bankAccountEventStoreService;
  private final BankAccountMapper bankAccountMapper;

  @SneakyThrows
  public void handler(Command command) throws AggregateNotFoundServiceException, OptimisticConcurrencyServiceException, DuplicatedAggregateServiceException, BankAccountServiceException {
    log.debug("Command type: {} Command aggregate_id: {}", command.getType(), command.getAggregateId());

    // Retrieve the whole event history by a specific aggregate id
    List<Event> events = this.retrieveEventsByAggregateId(command.getAggregateId());

    // Recreate the last aggregate snapshot replaying the whole event history by a specific aggregate id
    BankAccountAggregate bankAccountAggregate = new BankAccountAggregate(command.getAggregateId(), events);

    switch (command.getType()) {
      case CREATE_BANK_ACCOUNT_COMMAND:
        CreateBankAccountCommand createBankAccountCommand = (CreateBankAccountCommand) command;
        final BankAccountDTO bankAccountDTO = createBankAccountCommand.getData();
        if (bankAccountDTO.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
          throw new BankAccountServiceException("Balance must be equal or greater than 0");
        }
        this.process(createBankAccountCommand);
        break;
      case DEPOSIT_BANK_ACCOUNT_COMMAND:
        DepositBankAccountCommand depositBankAccountCommand = (DepositBankAccountCommand) command;
        final DepositBankAccountDTO depositBankAccountDTO = depositBankAccountCommand.getData();
        if (depositBankAccountDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
          throw new BankAccountServiceException("Amount must be greater than 0");
        }
        if (bankAccountAggregate.getBankAccountDTO() == null && bankAccountAggregate.getVersion() == null) {
          throw new AggregateNotFoundServiceException("Bank account " + bankAccountAggregate.getId() + " not found");
        }
        this.process(depositBankAccountCommand);
        break;
      case WITHDRAW_BANK_ACCOUNT_COMMAND:
        WithdrawBankAccountCommand withdrawBankAccountCommand = (WithdrawBankAccountCommand) command;
        final WithdrawBankAccountDTO withdrawBankAccountDTO = withdrawBankAccountCommand.getData();
        if (withdrawBankAccountDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
          throw new BankAccountServiceException("Amount must be greater than 0");
        }
        if (bankAccountAggregate.getBankAccountDTO() == null && bankAccountAggregate.getVersion() == null) {
          throw new AggregateNotFoundServiceException("Bank account " + bankAccountAggregate.getId() + " not found");
        }
        if (bankAccountAggregate.getBankAccountDTO().getBalance().subtract(withdrawBankAccountDTO.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
          throw new BankAccountServiceException("Insufficient balance");
        }
        this.process(withdrawBankAccountCommand);
        break;
    }

  }

  public void process(CreateBankAccountCommand command) {
    BankAccountCratedEvent event = this.bankAccountMapper.toEvent(command);
    bankAccountEventStoreService.appendEvent(event, null);
  }

  public void process(DepositBankAccountCommand command) {
    BankAccountDepositedEvent event = this.bankAccountMapper.toEvent(command);
    event.setVersion(command.getVersion() + 1);
    bankAccountEventStoreService.appendEvent(event, command.getVersion());
  }

  public void process(WithdrawBankAccountCommand command) {
    BankAccountWithdrawnEvent event = this.bankAccountMapper.toEvent(command);
    event.setVersion(command.getVersion() + 1);
    bankAccountEventStoreService.appendEvent(event, command.getVersion());
  }

  @SneakyThrows
  private List<Event> retrieveEventsByAggregateId(UUID aggregateId) {
    List<Event> events = new ArrayList<>();
    List<ResolvedEvent> resolvedEvents = this.bankAccountEventStoreService.retrieveEventsByAggregateId(aggregateId);
    for (ResolvedEvent resolvedEvent : resolvedEvents) {
      RecordedEvent originalEvent = resolvedEvent.getOriginalEvent();
      switch (originalEvent.getEventType()) {
        case BANK_ACCOUNT_CREATED_EVENT_V1:
          events.add(originalEvent.getEventDataAs((BankAccountCratedEvent.class)));
          break;
        case BANK_ACCOUNT_DEPOSITED_EVENT_V1:
          events.add(originalEvent.getEventDataAs((BankAccountDepositedEvent.class)));
          break;
        case BANK_ACCOUNT_WITHDRAWN_EVENT_V1:
          events.add(originalEvent.getEventDataAs((BankAccountWithdrawnEvent.class)));
          break;
      }
    }
    return events;
  }
}
