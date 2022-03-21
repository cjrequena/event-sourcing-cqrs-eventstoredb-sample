package com.cjrequena.sample.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 *
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

  /**
   *
   * @param value
   * @param generator
   * @param provider
   * @throws IOException
   */
  @Override
  public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
    generator.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
  }
}
