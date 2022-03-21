package com.cjrequena.sample.aggregate;

import com.cjrequena.sample.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public abstract class Aggregate {

  protected UUID id;
  protected String name;
  protected Long version;
  protected List<Event> events = new ArrayList<>();

  public Aggregate(UUID aggregateId, String aggregateName, List<Event> events) {
    Objects.requireNonNull(aggregateId);
    Objects.requireNonNull(events);
    this.id = aggregateId;
    this.name = aggregateName;
    this.events = events;
    replayEvents(this.events);
  }

  public Aggregate(UUID aggregateId, String aggregateName) {
    this(aggregateId, aggregateName, Collections.emptyList());
  }

  protected abstract void replayEvents(List<Event> events);
}
