package com.cjrequena.sample.service;

import com.cjrequena.sample.aggregate.BankAccountAggregate;
import com.cjrequena.sample.command.*;
import com.cjrequena.sample.dto.BankAccountDTO;
import com.cjrequena.sample.dto.DepositBankAccountDTO;
import com.cjrequena.sample.dto.WithdrawBankAccountDTO;
import com.cjrequena.sample.event.BankAccountCratedEvent;
import com.cjrequena.sample.event.BankAccountDepositedEvent;
import com.cjrequena.sample.event.BankAccountWithdrawnEvent;
import com.cjrequena.sample.event.Event;
import com.cjrequena.sample.exception.service.*;
import com.cjrequena.sample.mapper.BankAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BankAccountCommandService {

  private final EventStoreService bankAccountEventStoreService;
  private final BankAccountMapper bankAccountMapper;

  public void handler(Command command)
    throws AggregateNotFoundServiceException, OptimisticConcurrencyServiceException, DuplicatedAggregateServiceException, BankAccountServiceException {
    log.debug("Command type: {} Command aggregate_id: {}", command.getType(), command.getAggregateId());
    // Retrieve the whole event history by a specific aggregate id
    List<Event> events = this.bankAccountEventStoreService.retrieveEventsByAggregateId(command.getAggregateId());
    // Recreate the last aggregate snapshot replaying the whole event history by a specific aggregate id
    BankAccountAggregate bankAccountAggregate = new BankAccountAggregate(command.getAggregateId(), events);
    // Process the command
    this.process(command, bankAccountAggregate);
  }

  @SneakyThrows
  public void process(Command command, BankAccountAggregate bankAccountAggregate) {

    if (command.getType().equals(ECommandType.CREATE_BANK_ACCOUNT_COMMAND)) {
      CreateBankAccountCommand createBankAccountCommand = (CreateBankAccountCommand) command;
      final BankAccountDTO data = createBankAccountCommand.getData();
      if (data.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
        throw new BankAccountServiceException("Balance must be equal or greater than 0");
      }
      this.process(createBankAccountCommand);
    } else if (command.getType().equals(ECommandType.DEPOSIT_BANK_ACCOUNT_COMMAND)) {
      DepositBankAccountCommand depositBankAccountCommand = (DepositBankAccountCommand) command;
      final DepositBankAccountDTO data = depositBankAccountCommand.getData();
      if (data.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new BankAccountServiceException("Amount must be greater than 0");
      }
      if (bankAccountAggregate.getBankAccountDTO() == null && bankAccountAggregate.getVersion() == null) {
        throw new AggregateNotFoundServiceException("Bank account " + bankAccountAggregate.getId() +  " not found");
      }
      this.process(depositBankAccountCommand);
    } else if (command.getType().equals(ECommandType.WITHDRAW_BANK_ACCOUNT_COMMAND)) {
      WithdrawBankAccountCommand withdrawBankAccountCommand = (WithdrawBankAccountCommand) command;
      final WithdrawBankAccountDTO data = withdrawBankAccountCommand.getData();
      if (data.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new BankAccountServiceException("Amount must be greater than 0");
      }
      if (bankAccountAggregate.getBankAccountDTO() == null && bankAccountAggregate.getVersion() == null) {
        throw new AggregateNotFoundServiceException("Bank account " + bankAccountAggregate.getId() +  " not found");
      }
      if (bankAccountAggregate.getBankAccountDTO().getBalance().subtract(data.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
        throw new BankAccountServiceException("Insufficient balance");
      }
      this.process(withdrawBankAccountCommand);
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
}
