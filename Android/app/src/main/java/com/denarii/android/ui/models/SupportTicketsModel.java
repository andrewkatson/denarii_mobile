package com.denarii.android.ui.models;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SupportTicketsModel {
  private final Supplier<List<SupportTicketModel>> getSupportTicketModels;

  private final Function<String, Void> navigateToSupportTicketDetailsFunction;

  private final Supplier<Void> goToCreateSupportTicket;

  private final Supplier<Void> updateSupportTicketModels;

  public SupportTicketsModel(
      Supplier<List<SupportTicketModel>> getSupportTicketsModels,
      Function<String, Void> navigateToSupportTicketDetailsFunction,
      Supplier<Void> goToCreateSupportTicket,
      Supplier<Void> getSupportTicketModels) {
    this.getSupportTicketModels = getSupportTicketsModels;
    this.navigateToSupportTicketDetailsFunction = navigateToSupportTicketDetailsFunction;
    this.goToCreateSupportTicket = goToCreateSupportTicket;
    this.updateSupportTicketModels = getSupportTicketModels;
  }

  public Supplier<List<SupportTicketModel>> getGetSupportTicketModels() {
    return getSupportTicketModels;
  }

  public Supplier<Void> getGoToCreateSupportTicket() {
    return goToCreateSupportTicket;
  }

  public Function<String, Void> getNavigateToSupportTicketDetailsFunction() {
    return navigateToSupportTicketDetailsFunction;
  }

  public Supplier<Void> getUpdateSupportTicketModels() {
    return updateSupportTicketModels;
  }
}
