package com.cjrequena.sample.configuration;

import com.cjrequena.sample.common.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

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

  //  spring.jackson.serialization.FAIL_ON_EMPTY_BEANS=false
  //  spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false
  //  spring.jackson.deserialization.FAIL_ON_UNKNOWN_PROPERTIES=false
  //  spring.jackson.deserialization.ACCEPT_SINGLE_VALUE_AS_ARRAY=true
  //  spring.jackson.mapper.ACCEPT_CASE_INSENSITIVE_PROPERTIES=true
  //  spring.jackson.defaultPropertyInclusion=NON_NULL

  /**
   * Jackson builder.
   * @return the jackson2 object mapper builder
   */
  @Bean
  public Jackson2ObjectMapperBuilder jacksonBuilder() {
    final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    return builder;
  }

  @Bean(name = {"objectMapper"})
  @Primary
  ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    return builder
      .serializationInclusion(NON_NULL)
      .serializationInclusion(NON_EMPTY)
      .failOnEmptyBeans(false)
      .failOnUnknownProperties(false)
      .featuresToEnable(
        MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,
        DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .featuresToDisable(
        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
        MapperFeature.AUTO_DETECT_IS_GETTERS)
      .build()
      .setDateFormat(new SimpleDateFormat(Constants.DATE_TIME_FORMAT))
      .registerModule(new JavaTimeModule());
  }
}
