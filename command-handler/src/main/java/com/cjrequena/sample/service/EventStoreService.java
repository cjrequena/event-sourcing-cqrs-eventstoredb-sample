package com.cjrequena.sample.service;

import com.cjrequena.sample.common.EventStoreDBUtils;
import com.cjrequena.sample.configuration.EventStoreDBConfiguration;
import com.cjrequena.sample.event.BankAccountCratedEvent;
import com.cjrequena.sample.event.BankAccountDepositedEvent;
import com.cjrequena.sample.event.BankAccountWithdrawnEvent;
import com.cjrequena.sample.event.Event;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.eventstore.dbclient.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.cjrequena.sample.common.Constants.*;
import static com.eventstore.dbclient.ExpectedRevision.NO_STREAM;
import static com.eventstore.dbclient.ExpectedRevision.expectedRevision;

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
public class EventStoreService {

  private final EventStoreDBClient eventStoreDBClient;
  private final EventStoreDBConfiguration eventStoreDBConfiguration;

  @SneakyThrows
  public void appendEvent(Event event) {
    try {
      Objects.requireNonNull(event);
      EventData eventData = EventData.builderAsJson(event.getType().getValue(), event).build();
      AppendToStreamOptions options =
        AppendToStreamOptions.get()
          .expectedRevision(
            (event.getVersion() != null && event.getVersion() >= 0) ? expectedRevision(event.getVersion()) : NO_STREAM);
      eventStoreDBClient
        .appendToStream(EventStoreDBUtils.toStream(event.getAggregateId()), options, eventData)
        .get()
        .getNextExpectedRevision()
        .getValueUnsigned();
    } catch (ExecutionException ex) {
      if (ex.getCause() instanceof WrongExpectedVersionException) {
        WrongExpectedVersionException innerException = (WrongExpectedVersionException) ex.getCause();
        long actualRevision = innerException.getActualVersion().getValueUnsigned();
        log.debug(
          "Optimistic concurrency control error in aggregate {}: actual version is {} but expected {}",
          event.getAggregateId(),
          actualRevision,
          event.getVersion());
        throw new OptimisticConcurrencyServiceException(
          "Optimistic concurrency control error in aggregate :: " + event.getAggregateId() + " actual version " + actualRevision + "doesn't match expected version :: "
            + event.getVersion());
      }
      throw ex;
    }

  }

  @SneakyThrows
  public List<Event> retrieveEventsByAggregateId(UUID aggregateId) {
    try {
      Objects.requireNonNull(aggregateId);
      log.debug("Reading events for aggregate {}", aggregateId);
      List<ResolvedEvent> resolvedEvents;
      List<Event> events = new ArrayList<>();
      ReadResult result = eventStoreDBClient.readStream(EventStoreDBUtils.toStream(aggregateId)).get();
      resolvedEvents = result.getEvents();
      for (ResolvedEvent resolvedEvent : resolvedEvents) {
        RecordedEvent originalEvent = resolvedEvent.getOriginalEvent();
        switch (originalEvent.getEventType()) {
          case BANK_ACCOUNT_CREATED_EVENT_V1:
            events.add( originalEvent.getEventDataAs((BankAccountCratedEvent.class)));
            break;
          case BANK_ACCOUNT_DEPOSITED_EVENT_V1:
            events.add( originalEvent.getEventDataAs((BankAccountDepositedEvent.class)));
            break;
          case BANK_ACCOUNT_WITHDRAWN_EVENT_V1:
            events.add( originalEvent.getEventDataAs((BankAccountWithdrawnEvent.class)));
            break;
        }
      }
      return events;
    } catch (ExecutionException ex) {
      Throwable innerException = ex.getCause();
      if (innerException instanceof StreamNotFoundException) {
        log.debug("No events for aggregate {}", aggregateId);
        return Collections.emptyList();
      }
      throw ex;
    }
  }

}
