package com.cjrequena.sample.common.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 *
 */
@Log4j2
public class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

  /**
   *
   * @param parser
   * @param context
   * @return
   * @throws IOException
   */
  @Override
  public OffsetDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    try {
      return OffsetDateTime.parse(parser.readValueAs(String.class), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    } catch (Exception ex) {
      log.error("{}", ex.getMessage() + " - Invalid Date Format");
      throw ex;
    }
  }
}
