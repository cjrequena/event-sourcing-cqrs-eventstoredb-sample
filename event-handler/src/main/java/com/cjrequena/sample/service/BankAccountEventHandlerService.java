package com.cjrequena.sample.service;

import com.cjrequena.sample.aggregate.BankAccountAggregate;
import com.cjrequena.sample.db.entity.BankAccountEntity;
import com.cjrequena.sample.db.repository.BankAccountRepository;
import com.cjrequena.sample.event.*;
import com.cjrequena.sample.mapper.BankAccountMapper;
import com.eventstore.dbclient.RecordedEvent;
import com.eventstore.dbclient.ResolvedEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.cjrequena.sample.common.Constants.*;

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

  @SneakyThrows
  public void handler(RecordedEvent recordedEvent) {
    log.debug("Processing event {}", recordedEvent);

    Event event = null;
    switch (recordedEvent.getEventType()) {
      case BANK_ACCOUNT_CREATED_EVENT_V1:
        event = recordedEvent.getEventDataAs((BankAccountCratedEvent.class));
        break;
      case BANK_ACCOUNT_DEPOSITED_EVENT_V1:
        event = recordedEvent.getEventDataAs((BankAccountDepositedEvent.class));
        break;
      case BANK_ACCOUNT_WITHDRAWN_EVENT_V1:
        event = recordedEvent.getEventDataAs((BankAccountWithdrawnEvent.class));
        break;
    }

    // Retrieve the whole event history by a specific aggregate id
    List<Event> events = this.retrieveEventsByAggregateId(Objects.requireNonNull(event).getAggregateId());

    // Recreate the last aggregate snapshot replaying the whole event history by a specific aggregate id
    BankAccountAggregate bankAccountAggregate = new BankAccountAggregate(event.getAggregateId(), events);

    BankAccountEntity bankAccountEntity = bankAccountMapper.toEntity(bankAccountAggregate.getBankAccountDTO());

    bankAccountEntity.setVersion(bankAccountAggregate.getVersion());

    this.bankAccountRepository.save(bankAccountEntity);
  }

  @SneakyThrows
  private List<Event> retrieveEventsByAggregateId(UUID aggregateId) {
    List<Event> events = new ArrayList<>();
    List<ResolvedEvent> resolvedEvents = this.eventStoreService.retrieveEventsByAggregateId(aggregateId);
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
