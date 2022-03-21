package com.cjrequena.sample;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Log4j2
@SpringBootApplication
@EnableScheduling
public class EventHandlerMainApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(EventHandlerMainApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
  }
}
