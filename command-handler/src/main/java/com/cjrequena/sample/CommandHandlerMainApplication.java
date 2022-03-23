package com.cjrequena.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CommandHandlerMainApplication implements CommandLineRunner {

//  @Autowired
//  EventStoreService bankAccountEventStoreService;

  public static void main(String[] args) {
    SpringApplication.run(CommandHandlerMainApplication.class, args);
  }

  @Override
  public void run(String... args) {

//    BankAccountDTO bankAccountDTO = new BankAccountDTO();
//    bankAccountDTO.setId(UUID.randomUUID());
//    bankAccountDTO.setBalance(BigDecimal.ZERO);
//    bankAccountDTO.setOwner("cjrequena");
//    //bankAccountDTO.setVersion(0L);
//    BankAccountCratedEvent bankAccountCratedEvent = BankAccountCratedEvent
//      .builder()
//      .aggregateId(bankAccountDTO.getId())
//      .data(bankAccountDTO)
//      .build();
//    this.bankAccountEventStoreService.appendEvent(bankAccountCratedEvent, bankAccountDTO.getVersion());
//
//    DepositBankAccountDTO depositBankAccountDTO = new DepositBankAccountDTO();
//    depositBankAccountDTO.setAccountId(bankAccountDTO.getId());
//    depositBankAccountDTO.setAmount(BigDecimal.valueOf(10));
//    BankAccountDepositedEvent bankAccountDepositedEvent = BankAccountDepositedEvent
//      .builder()
//      .aggregateId(bankAccountDTO.getId())
//      .version(0L)
//      .data(depositBankAccountDTO)
//      .build();
//    this.bankAccountEventStoreService.appendEvent(bankAccountDepositedEvent, bankAccountDepositedEvent.getRevision());
//
//    depositBankAccountDTO = new DepositBankAccountDTO();
//    depositBankAccountDTO.setAccountId(bankAccountDTO.getId());
//    depositBankAccountDTO.setAmount(BigDecimal.valueOf(20));
//    bankAccountDepositedEvent = BankAccountDepositedEvent
//      .builder()
//      .aggregateId(bankAccountDTO.getId())
//      .version(1L)
//      .data(depositBankAccountDTO)
//      .build();
//    this.bankAccountEventStoreService.appendEvent(bankAccountDepositedEvent, bankAccountDepositedEvent.getRevision());
//
//    depositBankAccountDTO = new DepositBankAccountDTO();
//    depositBankAccountDTO.setAccountId(bankAccountDTO.getId());
//    depositBankAccountDTO.setAmount(BigDecimal.valueOf(30));
//    bankAccountDepositedEvent = BankAccountDepositedEvent
//      .builder()
//      .aggregateId(bankAccountDTO.getId())
//      .version(2L)
//      .data(depositBankAccountDTO)
//      .build();
//    this.bankAccountEventStoreService.appendEvent(bankAccountDepositedEvent, bankAccountDepositedEvent.getRevision());
//
//    depositBankAccountDTO = new DepositBankAccountDTO();
//    depositBankAccountDTO.setAccountId(bankAccountDTO.getId());
//    depositBankAccountDTO.setAmount(BigDecimal.valueOf(40));
//    bankAccountDepositedEvent = BankAccountDepositedEvent
//      .builder()
//      .aggregateId(bankAccountDTO.getId())
//      .version(3L)
//      .data(depositBankAccountDTO)
//      .build();
//    this.bankAccountEventStoreService.appendEvent(bankAccountDepositedEvent, bankAccountDepositedEvent.getRevision());
//
//    List<Event> events = this.bankAccountEventStoreService.retrieveEventsByAggregateId(bankAccountDTO.getId());
//    for (Event event : events) {
//      log.debug(" {}", event);
//    }

  }
}
