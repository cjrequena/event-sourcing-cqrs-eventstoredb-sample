package com.cjrequena.sample.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 *
 */
public class LocalTimeSerializer extends JsonSerializer<LocalTime> {

  /**
   *
   * @param value
   * @param generator
   * @param provider
   * @throws IOException
   */
  @Override
  public void serialize(LocalTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
    generator.writeString(value.format(DateTimeFormatter.ISO_LOCAL_TIME));
  }
}
