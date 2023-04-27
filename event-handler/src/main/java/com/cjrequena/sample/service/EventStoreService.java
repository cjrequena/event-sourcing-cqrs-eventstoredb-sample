package com.cjrequena.sample.service;

import com.cjrequena.sample.common.EventStoreDBUtils;
import com.cjrequena.sample.configuration.EventStoreDBConfiguration;
import com.eventstore.dbclient.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

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
  public List<ResolvedEvent> retrieveEventsByAggregateId(UUID aggregateId) {
    try {
      Objects.requireNonNull(aggregateId);
      log.debug("Reading events for aggregate {}", aggregateId);
      ReadResult result = eventStoreDBClient.readStream(EventStoreDBUtils.toStream(aggregateId), ReadStreamOptions.get()
        .forwards()
        .fromStart()).get();
      return result.getEvents();
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
  public void subscribe(Consumer<RecordedEvent> consumer, String selector) {
    CreatePersistentSubscriptionToStreamOptions persistentSubscriptionSettings =
      CreatePersistentSubscriptionToStreamOptions.get()
        .fromStart()
        .resolveLinkTos()
        .namedConsumerStrategy(NamedConsumerStrategy.PINNED);

    try {
      persistentSubscriptionsClient
        .createToStream(selector, eventStoreDBConfiguration.getPersistentSubscription().getGroup(), persistentSubscriptionSettings)
        .get();
    } catch (ExecutionException ex) {
      if (ex.getCause() instanceof StatusRuntimeException innerException) {
        if (innerException.getStatus().getCode() == Status.ALREADY_EXISTS.getCode()) {
          log.info(innerException.getMessage());
        } else {
          throw ex;
        }
      }
    }

    SubscribePersistentSubscriptionOptions subscribePersistentSubscriptionOptions = SubscribePersistentSubscriptionOptions
      .get()
      .bufferSize(eventStoreDBConfiguration.getPersistentSubscription().getBufferSize());

    persistentSubscriptionsClient.subscribeToStream(
      selector,
      eventStoreDBConfiguration.getPersistentSubscription().getGroup(),
      subscribePersistentSubscriptionOptions,
      new PersistentSubscriptionListener() {

        @Override
        public void onEvent(PersistentSubscription subscription, int retryCount, ResolvedEvent resolvedEvent) {
          RecordedEvent recordedEvent = resolvedEvent.getEvent();
          log.debug(
            "Received event {}@{} from subscription {} and retry {}",
            recordedEvent.getStreamId(),
            recordedEvent.getRevision(),
            subscription.getSubscriptionId(),
            retryCount);
          try {
            consumer.accept(recordedEvent);
            subscription.ack(resolvedEvent);
          } catch (Exception e) {
            log.error(
              String.format(
                "Error processing event %s@%s from subscription %s: %s",
                recordedEvent.getStreamId(),
                recordedEvent.getRevision(),
                subscription.getSubscriptionId(),
                e.getMessage()),
              e);
          }
          subscription.ack(resolvedEvent);
        }

        @Override
        public void onError(PersistentSubscription subscription, Throwable throwable) {
          log.warn("Subscription was dropped due to " + throwable.getMessage());
        }

        @Override
        public void onCancelled(PersistentSubscription subscription) {
          log.warn("Subscription is cancelled");
        }
      });
  }

}
