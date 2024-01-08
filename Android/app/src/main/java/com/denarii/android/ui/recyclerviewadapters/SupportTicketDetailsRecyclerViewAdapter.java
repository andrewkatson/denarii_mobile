package com.denarii.android.ui.recyclerviewadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.denarii.android.R;
import com.denarii.android.ui.models.SupportTicketCommentModel;
import com.denarii.android.ui.models.SupportTicketDetailsModel;

public class SupportTicketDetailsRecyclerViewAdapter
    extends RecyclerView.Adapter<SupportTicketDetailsRecyclerViewAdapter.ViewHolder> {

  private final SupportTicketDetailsModel supportTicketDetailsModel;

  private SupportTicketCommentRecyclerViewAdapter supportTicketCommentsRecyclerViewAdapter;

  public SupportTicketDetailsRecyclerViewAdapter(
      SupportTicketDetailsModel supportTicketDetailsModel) {
    this.supportTicketDetailsModel = supportTicketDetailsModel;
  }

  // Usually involves inflating a layout from XML and returning the holder
  @NonNull
  @Override
  public SupportTicketDetailsRecyclerViewAdapter.ViewHolder onCreateViewHolder(
      ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    // Inflate the custom layout
    View contactView = inflater.inflate(R.layout.support_ticket_details_interior, parent, false);

    // Return a new holder instance
    return new SupportTicketDetailsRecyclerViewAdapter.ViewHolder(contactView);
  }

  // Involves populating data into the item through holder
  @Override
  public void onBindViewHolder(
      @NonNull SupportTicketDetailsRecyclerViewAdapter.ViewHolder holder, int position) {
    Button addNewCommentButton = holder.itemView.findViewById(R.id.commentBoxSubmit);

    addNewCommentButton.setOnClickListener(
        v -> {
          supportTicketDetailsModel.getAddNewComment().get();
        });

    Button resolveButton = holder.itemView.findViewById(R.id.resolveSupportTicket);

    resolveButton.setOnClickListener(
        v -> {
          supportTicketDetailsModel.getResolve().get();
        });

    Button deleteButton = holder.itemView.findViewById(R.id.deleteSupportTicket);

    deleteButton.setOnClickListener(
        v -> {
          supportTicketDetailsModel.getDelete().get();
        });

    // Create the recycler view for the comments linear layout
    RecyclerView commentsRecyclerView =
        (RecyclerView) holder.itemView.findViewById(R.id.supportTicketCommentsRecyclerView);

    supportTicketCommentsRecyclerViewAdapter =
        new SupportTicketCommentRecyclerViewAdapter(
            supportTicketDetailsModel.getGetComments().get(),
            supportTicketDetailsModel.getGetUserName().get());
    commentsRecyclerView.setAdapter(supportTicketCommentsRecyclerViewAdapter);
    commentsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

    supportTicketDetailsModel.getGetSupportTicket().apply(holder);
  }

  public void refresh() {
    updateSupportTicketCommentsRecyclerView();
  }

  private void updateSupportTicketCommentsRecyclerView() {
    int itemCountMinusOne = supportTicketCommentsRecyclerViewAdapter.getItemCount() - 1;
    for (int i = itemCountMinusOne; i >= 0; i--) {
      supportTicketCommentsRecyclerViewAdapter.notifyItemRemoved(i);
    }

    supportTicketDetailsModel.getUpdateCommentModels().get();

    int pos = 0;
    for (SupportTicketCommentModel unused : supportTicketDetailsModel.getGetComments().get()) {
      supportTicketCommentsRecyclerViewAdapter.notifyItemInserted(pos);
      pos += 1;
    }
  }

  public void setTitle(String title, ViewHolder viewHolder) {
    TextView supportTicketTitle = viewHolder.itemView.findViewById(R.id.title);
    supportTicketTitle.setText(title);
  }

  public void setDescription(String description, ViewHolder viewHolder) {
    TextView supportTicketDescription = viewHolder.itemView.findViewById(R.id.description);
    supportTicketDescription.setText(description);
  }

  // Returns the total count of items in the list
  @Override
  public int getItemCount() {
    return 1;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(View itemView) {
      super(itemView);
    }
  }
}
