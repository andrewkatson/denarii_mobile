package com.denarii.android.ui.models;

import com.denarii.android.ui.recyclerviewadapters.SellDenariiRecyclerViewAdapter;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class SellDenariiModel {
  private final Supplier<List<Ask>> getAsks;

  private final Supplier<List<OwnAsk>> getOwnAsks;

  private final Supplier<List<PendingSale>> getPendingSales;

  private final Function<Set<String>, Void> cancelAsks;

  private final Supplier<Void> clickSubmit;

  private final Supplier<Void> getCurrentAsks;

  private final Supplier<Void> getOwnDenariiAsks;

  private final Supplier<Void> getPendingSalesOfDenariiAsks;

  private final Function<SellDenariiRecyclerViewAdapter.ViewHolder, Void> setGoingPrice;

  public SellDenariiModel(
      Supplier<List<Ask>> getAsks,
      Supplier<List<OwnAsk>> getOwnAsks,
      Supplier<List<PendingSale>> getPendingSales,
      Function<Set<String>, Void> cancelAsks,
      Supplier<Void> clickSubmit,
      Supplier<Void> getCurrentAsks,
      Supplier<Void> getOwnDenariiAsks,
      Supplier<Void> getPendingSalesOfDenariiAsks,
      Function<SellDenariiRecyclerViewAdapter.ViewHolder, Void> setGoingPrice) {
    this.getAsks = getAsks;
    this.getOwnAsks = getOwnAsks;
    this.getPendingSales = getPendingSales;
    this.cancelAsks = cancelAsks;
    this.clickSubmit = clickSubmit;
    this.getCurrentAsks = getCurrentAsks;
    this.getOwnDenariiAsks = getOwnDenariiAsks;
    this.getPendingSalesOfDenariiAsks = getPendingSalesOfDenariiAsks;
    this.setGoingPrice = setGoingPrice;
  }

  public Supplier<List<Ask>> getGetAsks() {
    return getAsks;
  }

  public Supplier<List<OwnAsk>> getGetOwnAsks() {
    return getOwnAsks;
  }

  public Supplier<List<PendingSale>> getGetPendingSales() {
    return getPendingSales;
  }

  public Function<Set<String>, Void> getCancelAsks() {
    return cancelAsks;
  }

  public Supplier<Void> getClickSubmit() {
    return clickSubmit;
  }

  public Supplier<Void> getGetCurrentAsks() {
    return getCurrentAsks;
  }

  public Supplier<Void> getGetOwnDenariiAsks() {
    return getOwnDenariiAsks;
  }

  public Supplier<Void> getGetPendingSalesOfDenariiAsks() {
    return getPendingSalesOfDenariiAsks;
  }

  public Function<SellDenariiRecyclerViewAdapter.ViewHolder, Void> getSetGoingPrice() {
    return setGoingPrice;
  }
}
