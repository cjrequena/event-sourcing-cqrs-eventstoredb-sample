package com.cjrequena.sample.service;

import com.cjrequena.sample.common.EventStoreDBUtils;
import com.cjrequena.sample.configuration.EventStoreDBConfiguration;
import com.cjrequena.sample.event.Event;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.eventstore.dbclient.*;
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
  public void appendEvent(Event event, Long expectedVersion) {
    try {
      Objects.requireNonNull(event);
      EventData eventData = EventData.builderAsJson(event.getType().getValue(), event).build();
      AppendToStreamOptions options = AppendToStreamOptions.get().expectedRevision(expectedVersion != null ? ExpectedRevision.expectedRevision(expectedVersion) : ExpectedRevision.noStream());
      WriteResult writeResult = eventStoreDBClient
        .appendToStream(EventStoreDBUtils.toStream(event.getAggregateId()), options, eventData)
        .get();

      log.info("Event stored with id [{}] and version [{}]", event.getAggregateId().toString(), writeResult.getNextExpectedRevision().toString());
    } catch (ExecutionException ex) {
      if (ex.getCause() instanceof WrongExpectedVersionException innerException) {
        String actualRevision = innerException.getActualVersion().toString();
        log.debug(
          "Optimistic concurrency control error in aggregate {}: actual version is {} but expected {}",
          event.getAggregateId(),
          actualRevision,
          event.getVersion());
        throw new OptimisticConcurrencyServiceException(
          "Optimistic concurrency control error in aggregate " + event.getAggregateId() + " :: actual version " + actualRevision + " :: doesn't match expected version :: " + expectedVersion);
      }
      throw ex;
    }

  }

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

}
