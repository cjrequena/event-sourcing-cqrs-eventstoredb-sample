package com.cjrequena.sample;

import com.cjrequena.sample.Service.BankAccountEventHandlerService;
import com.cjrequena.sample.Service.EventStoreService;
import com.cjrequena.sample.event.EEventStreams;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@Log4j2
@SpringBootApplication
public class EventHandlerMainApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(EventHandlerMainApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
  }
}
