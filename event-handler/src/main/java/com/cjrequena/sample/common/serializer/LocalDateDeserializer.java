package com.cjrequena.sample.common.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.LocalDate;
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
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

  /**
   *
   * @param parser
   * @param context
   * @return
   * @throws IOException
   */
  @Override
  public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    try {
      return LocalDate.parse(parser.readValueAs(String.class), DateTimeFormatter.ISO_LOCAL_DATE);
    } catch (Exception ex) {
      log.error("{}", ex.getMessage() + " - Invalid Date Format");
      throw ex;
    }
  }
}
