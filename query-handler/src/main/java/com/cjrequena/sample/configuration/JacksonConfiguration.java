package com.cjrequena.sample.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.core.util.Json;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 */
@Configuration
public class JacksonConfiguration {
  /**
   * Jackson builder.
   * @return the jackson2 object mapper builder
   */
  @Bean
  public Jackson2ObjectMapperBuilder jacksonBuilder() {
    final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    return builder;
  }

  /**
   * Default Jackson
   * @return
   */
  @Bean
  public ObjectMapper objectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();

    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    objectMapper.disable(MapperFeature.AUTO_DETECT_IS_GETTERS);
    Json.mapper().disable(MapperFeature.AUTO_DETECT_IS_GETTERS);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    objectMapper.setDateFormat(simpleDateFormat);
    Json.mapper().setDateFormat(simpleDateFormat);

    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    Json.mapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    objectMapper.configure(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING, true);
    Json.mapper().configure(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING, true);

    objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    Json.mapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

    JavaTimeModule javaTimeModule = new JavaTimeModule();
    objectMapper.registerModule(javaTimeModule);
    Json.mapper().registerModule(javaTimeModule);

    return objectMapper;
  }
}
