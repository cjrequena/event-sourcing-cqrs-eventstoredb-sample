package com.cjrequena.sample.common;

import com.cjrequena.sample.event.EEventStreams;

import java.util.UUID;

public class EventStoreDBUtils {

  public static String toStream(UUID id){
    return EEventStreams.BANK_ACCOUNTS.getPrefix() + id;
  }
}
