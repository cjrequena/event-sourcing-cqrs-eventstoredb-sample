package com.cjrequena.sample.common;

import java.util.UUID;

public class EventStoreDBUtils {

  public static String toStream(UUID id){
    return Constants.BANK_ACCOUNT_AGGREGATE_EVENT_STORE_STREAM_PREFIX + id;
  }
}
