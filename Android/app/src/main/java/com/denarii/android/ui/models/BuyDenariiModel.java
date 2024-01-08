package com.denarii.android.ui.models;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class BuyDenariiModel {
  private final Supplier<List<Ask>> getAsks;

  private final Supplier<List<QueuedBuy>> getQueuedBuys;

  private final Supplier<Void> clickSubmit;

  private final Function<Set<String>, Void> cancelBuys;

  private final Supplier<Void> getCurrentAsks;

  private final Supplier<Void> getQueuedDenariiAskBuys;

  public BuyDenariiModel(
      Supplier<List<Ask>> getAsks,
      Supplier<List<QueuedBuy>> getQueuedBuys,
      Supplier<Void> clickSubmit,
      Function<Set<String>, Void> cancelBuys,
      Supplier<Void> getCurrentAsks,
      Supplier<Void> getQueuedDenariiAskBuys) {
    this.getAsks = getAsks;
    this.getQueuedBuys = getQueuedBuys;
    this.clickSubmit = clickSubmit;
    this.cancelBuys = cancelBuys;
    this.getCurrentAsks = getCurrentAsks;
    this.getQueuedDenariiAskBuys = getQueuedDenariiAskBuys;
  }

  public Supplier<List<Ask>> getGetAsks() {
    return getAsks;
  }

  public Supplier<List<QueuedBuy>> getGetQueuedBuys() {
    return getQueuedBuys;
  }

  public Supplier<Void> getClickSubmit() {
    return clickSubmit;
  }

  public Function<Set<String>, Void> getCancelBuys() {
    return cancelBuys;
  }

  public Supplier<Void> getGetCurrentAsks() {
    return getCurrentAsks;
  }

  public Supplier<Void> getGetQueuedDenariiAskBuys() {
    return getQueuedDenariiAskBuys;
  }
}
