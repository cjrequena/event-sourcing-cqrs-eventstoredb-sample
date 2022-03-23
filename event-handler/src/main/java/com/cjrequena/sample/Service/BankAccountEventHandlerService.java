package com.cjrequena.sample.Service;

import com.cjrequena.sample.aggregate.BankAccountAggregate;
import com.cjrequena.sample.db.entity.BankAccountEntity;
import com.cjrequena.sample.db.repository.BankAccountRepository;
import com.cjrequena.sample.event.EEventStreams;
import com.cjrequena.sample.event.Event;
import com.cjrequena.sample.mapper.BankAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

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
//@ConditionalOnProperty(name = "subscription.enabled", havingValue = "true")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BankAccountEventHandlerService {

  private final BankAccountRepository bankAccountRepository;
  private final BankAccountMapper bankAccountMapper;
  private final EventStoreService eventStoreService;

  @PostConstruct
  public void init() {
    eventStoreService.subscribe(this::handler, EEventStreams.BANK_ACCOUNTS.getCategorySelector());
  }

  public void handler(Event event) {
    Objects.requireNonNull(event);
    log.debug("Processing event {}", event);
    List<Event> events = eventStoreService.retrieveEventsByAggregateId(event.getAggregateId());
    BankAccountAggregate bankAccountAggregate = new BankAccountAggregate(event.getAggregateId(), events);
    log.debug(" {}", bankAccountAggregate);
    BankAccountEntity bankAccountEntity = bankAccountMapper.toEntity(bankAccountAggregate.getBankAccountDTO());
    bankAccountEntity.setVersion(bankAccountAggregate.getVersion());
    this.bankAccountRepository.save(bankAccountEntity);
  }
}
