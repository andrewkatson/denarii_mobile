package com.denarii.android.ui.models;

import com.denarii.android.ui.recyclerviewadapters.SupportTicketDetailsRecyclerViewAdapter;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SupportTicketDetailsModel {

  private final Supplier<List<SupportTicketCommentModel>> getComments;

  private final Supplier<Void> addNewComment;

  private final Supplier<Void> resolve;

  private final Supplier<Void> delete;

  private final Supplier<String> getUserName;

  private final Supplier<Void> updateCommentModels;

  private final Function<SupportTicketDetailsRecyclerViewAdapter.ViewHolder, Void> getSupportTicket;

  public SupportTicketDetailsModel(
      Supplier<List<SupportTicketCommentModel>> getComments,
      Supplier<Void> addNewComment,
      Supplier<Void> resolve,
      Supplier<Void> delete,
      Supplier<String> getUserName,
      Supplier<Void> updateCommentModels,
      Function<SupportTicketDetailsRecyclerViewAdapter.ViewHolder, Void> getSupportTicket) {
    this.getComments = getComments;
    this.addNewComment = addNewComment;
    this.resolve = resolve;
    this.delete = delete;
    this.getUserName = getUserName;
    this.updateCommentModels = updateCommentModels;
    this.getSupportTicket = getSupportTicket;
  }

  public Supplier<List<SupportTicketCommentModel>> getGetComments() {
    return getComments;
  }

  public Supplier<Void> getAddNewComment() {
    return addNewComment;
  }

  public Supplier<Void> getDelete() {
    return delete;
  }

  public Supplier<Void> getResolve() {
    return resolve;
  }

  public Supplier<String> getGetUserName() {
    return getUserName;
  }

  public Supplier<Void> getUpdateCommentModels() {
    return updateCommentModels;
  }

  public Function<SupportTicketDetailsRecyclerViewAdapter.ViewHolder, Void> getGetSupportTicket() {
    return getSupportTicket;
  }
}
