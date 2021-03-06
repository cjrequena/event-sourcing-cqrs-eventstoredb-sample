package com.cjrequena.sample.configuration;

import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.EventStoreDBClientSettings;
import com.eventstore.dbclient.EventStoreDBConnectionString;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties("eventstoredb")
@RequiredArgsConstructor
public class EventStoreDBConfiguration {

  private String connectionString;
  private boolean autoSubscribe = true;
  private PersistentSubscription persistentSubscription = new PersistentSubscription();

  @Data
  public static class PersistentSubscription {
    private String group;
    private int bufferSize;
  }

  @Bean
  public EventStoreDBClient eventStoreDBClient() {
    EventStoreDBClientSettings settings = EventStoreDBConnectionString.parseOrThrow(connectionString);
    return EventStoreDBClient.create(settings);
  }
}
