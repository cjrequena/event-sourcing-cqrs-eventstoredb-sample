package com.cjrequena.sample.event;

import com.cjrequena.sample.common.serializer.OffsetDateTimeDeserializer;
import com.cjrequena.sample.common.serializer.OffsetDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
@ToString(callSuper = true)
public abstract class Event<T> {

  // Unique id for the specific message. This id is globally unique
  @NotNull(message = "id is mandatory")
  @Pattern(regexp = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", message = "Format is not valid")
  @Getter(onMethod = @__({@JsonProperty("id")}))
  protected UUID id;

  // Identifies the context in which an event happened.
  protected String source;

  // The version of the CloudEvents specification which the event uses.
  protected final String specVersion = "1.0";

  // Type of message
  protected EEventType type;

  // Content type of the data value. Must adhere to RFC 2046 format.
  public String dataContentType;

  // Describes the subject of the event in the context of the event producer (identified by source).
  protected String subject;

  // Date and time for when the message was published
  @JsonProperty(value = "time", required = true)
  @NotNull(message = "Time is mandatory")
  @Pattern(regexp = "uuuu-MM-dd'T'HH:mm:ssXXX", message = "Format is not valid")
  @Getter(onMethod = @__({@JsonProperty(value = "time", required = true)}))
  @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  protected OffsetDateTime time;

  // The event payload.
  protected T data;

  // Base64 encoded event payload. Must adhere to RFC4648.
  protected String dataBase64;

  // Identifies the schema that data adheres to.
  protected EEventSchemaType dataSchema;

  // The aggregate_id for the specific message.
  @JsonProperty(value = "aggregate_id", required = true)
  @NotNull(message = "aggregate_id is mandatory")
  @Pattern(regexp = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", message = "Format is not valid")
  @Getter(onMethod = @__({@JsonProperty("aggregate_id")}))
  protected UUID aggregateId;

  //
  protected Long version;

}
