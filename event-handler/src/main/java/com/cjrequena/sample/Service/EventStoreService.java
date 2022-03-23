package com.cjrequena.sample.Service;

import com.cjrequena.sample.common.EventStoreDBUtils;
import com.cjrequena.sample.configuration.EventStoreDBConfiguration;
import com.cjrequena.sample.event.BankAccountCratedEvent;
import com.cjrequena.sample.event.BankAccountDepositedEvent;
import com.cjrequena.sample.event.BankAccountWithdrawnEvent;
import com.cjrequena.sample.event.Event;
import com.eventstore.dbclient.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventStoreService {

  private final EventStoreDBClient eventStoreDBClient;
  private final EventStoreDBPersistentSubscriptionsClient persistentSubscriptionsClient;
  private final EventStoreDBConfiguration eventStoreDBConfiguration;

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
    } catch (ExecutionException ex) {
      Throwable innerException = ex.getCause();
      if (innerException instanceof StreamNotFoundException) {
        log.debug("No events for aggregate {}", aggregateId);
        return Collections.emptyList();
      }
      throw ex;
    }
  }

  @SneakyThrows
  public void subscribe(Consumer<Event> consumer, String selector) {
    PersistentSubscriptionSettings persistentSubscriptionSettings = PersistentSubscriptionSettings
      .builder()
      .fromStart()
      .resolveLinkTos()
      .consumerStrategy(ConsumerStrategy.Pinned)
      .build();

    try {
      persistentSubscriptionsClient
        .create(selector, eventStoreDBConfiguration.getPersistentSubscription().getGroup(), persistentSubscriptionSettings)
        .get();
    } catch (ExecutionException ex) {
      if (ex.getCause() instanceof StatusRuntimeException) {
        StatusRuntimeException innerException = (StatusRuntimeException) ex.getCause();
        if (innerException.getStatus().getCode() == Status.ALREADY_EXISTS.getCode()) {
          log.info(innerException.getMessage());
        } else {
          throw ex;
        }
      }
    }

    SubscribePersistentSubscriptionOptions subscribePersistentSubscriptionOptions = SubscribePersistentSubscriptionOptions
      .get()
      .setBufferSize(eventStoreDBConfiguration.getPersistentSubscription().getBufferSize());

    persistentSubscriptionsClient.subscribe(
      selector,
      eventStoreDBConfiguration.getPersistentSubscription().getGroup(),
      subscribePersistentSubscriptionOptions,
      new PersistentSubscriptionListener() {

        @Override
        public void onEvent(PersistentSubscription subscription, ResolvedEvent resolvedEvent) {
          RecordedEvent recordedEvent = resolvedEvent.getEvent();
          log.debug(
            "Received event {}@{} from subscription {}",
            recordedEvent.getStreamId(),
            recordedEvent.getStreamRevision().getValueUnsigned(),
            subscription.getSubscriptionId());
          try {
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
            consumer.accept(event);
            subscription.ack(resolvedEvent);
          } catch (Exception e) {
            log.error(
              String.format(
                "Error processing event %s@%s from subscription %s: %s",
                recordedEvent.getStreamId(),
                recordedEvent.getStreamRevision().getValueUnsigned(),
                subscription.getSubscriptionId(),
                e.getMessage()),
              e);
          }
          subscription.ack(resolvedEvent);
        }

        @Override
        public void onError(PersistentSubscription subscription, Throwable throwable) {
          System.out.println("Subscription was dropped due to " + throwable.getMessage());
        }

        @Override
        public void onCancelled(PersistentSubscription subscription) {
          System.out.println("Subscription is cancelled");
        }
      });
  }

}
