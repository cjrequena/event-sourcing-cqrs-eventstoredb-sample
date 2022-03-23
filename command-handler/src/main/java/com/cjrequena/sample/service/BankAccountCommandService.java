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
import com.cjrequena.sample.exception.service.AggregateNotFoundServiceException;
import com.cjrequena.sample.exception.service.BankAccountServiceException;
import com.cjrequena.sample.exception.service.DuplicatedAggregateServiceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.cjrequena.sample.mapper.BankAccountMapper;
import lombok.RequiredArgsConstructor;
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
    this.process(command, bankAccountAggregate);
  }

  
  public void process(Command command, BankAccountAggregate bankAccountAggregate)
    throws OptimisticConcurrencyServiceException, DuplicatedAggregateServiceException, AggregateNotFoundServiceException, BankAccountServiceException {

    final BankAccountDTO bankAccountDTO = bankAccountAggregate.getBankAccountDTO();

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
      this.process(depositBankAccountCommand);
    } else if (command.getType().equals(ECommandType.WITHDRAW_BANK_ACCOUNT_COMMAND)) {
      WithdrawBankAccountCommand withdrawBankAccountCommand = (WithdrawBankAccountCommand) command;
      final WithdrawBankAccountDTO data = withdrawBankAccountCommand.getData();
      if (data.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new BankAccountServiceException("Amount must be greater than 0");
      }
      if(bankAccountDTO.getBalance().subtract(data.getAmount()).compareTo(BigDecimal.ZERO)<=0){
        throw new BankAccountServiceException("Insufficient balance");
      }
      this.process(withdrawBankAccountCommand);
    }
  }

  
  public void process(CreateBankAccountCommand command)
    throws OptimisticConcurrencyServiceException, DuplicatedAggregateServiceException, AggregateNotFoundServiceException {
    BankAccountCratedEvent event = this.bankAccountMapper.toEvent(command);
    bankAccountEventStoreService.appendEvent(event);
  }

  
  public void process(DepositBankAccountCommand command)
    throws OptimisticConcurrencyServiceException, DuplicatedAggregateServiceException, AggregateNotFoundServiceException {

    BankAccountDepositedEvent event = this.bankAccountMapper.toEvent(command);
    bankAccountEventStoreService.appendEvent(event);
  }

  
  public void process(WithdrawBankAccountCommand command)
    throws OptimisticConcurrencyServiceException, DuplicatedAggregateServiceException, AggregateNotFoundServiceException {
    BankAccountWithdrawnEvent event = this.bankAccountMapper.toEvent(command);
    bankAccountEventStoreService.appendEvent(event);
  }
}
