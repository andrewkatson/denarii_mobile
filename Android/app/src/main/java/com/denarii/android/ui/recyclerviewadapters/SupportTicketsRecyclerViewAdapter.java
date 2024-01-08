package com.denarii.android.ui.recyclerviewadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.denarii.android.R;
import com.denarii.android.ui.models.SupportTicketModel;
import com.denarii.android.ui.models.SupportTicketsModel;

public class SupportTicketsRecyclerViewAdapter
    extends RecyclerView.Adapter<SupportTicketsRecyclerViewAdapter.ViewHolder> {

  private final SupportTicketsModel supportTicketsModel;

  private SupportTicketRecyclerViewAdapter supportTicketRecyclerViewAdapter = null;

  public SupportTicketsRecyclerViewAdapter(SupportTicketsModel supportTicketsModel) {
    this.supportTicketsModel = supportTicketsModel;
  }

  // Usually involves inflating a layout from XML and returning the holder
  @NonNull
  @Override
  public SupportTicketsRecyclerViewAdapter.ViewHolder onCreateViewHolder(
      ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    // Inflate the custom layout
    View contactView = inflater.inflate(R.layout.support_tickets_interior, parent, false);

    // Return a new holder instance
    return new SupportTicketsRecyclerViewAdapter.ViewHolder(contactView);
  }

  // Involves populating data into the item through holder
  @Override
  public void onBindViewHolder(
      @NonNull SupportTicketsRecyclerViewAdapter.ViewHolder holder, int position) {
    Button createSupportTicketButton = holder.itemView.findViewById(R.id.createSupportTicket);

    createSupportTicketButton.setOnClickListener(
        v -> supportTicketsModel.getGoToCreateSupportTicket().get());

    // Create the recycler view for the support tickets linear layout
    RecyclerView ticketsRecyclerView =
        (RecyclerView) holder.itemView.findViewById(R.id.supportTicketsSupportTicketsRecyclerView);

    supportTicketRecyclerViewAdapter =
        new SupportTicketRecyclerViewAdapter(
            supportTicketsModel.getGetSupportTicketModels().get(),
            (supportTicketId) -> {
              supportTicketsModel
                  .getNavigateToSupportTicketDetailsFunction()
                  .apply(supportTicketId);
              return null;
            });
    ticketsRecyclerView.setAdapter(supportTicketRecyclerViewAdapter);
    ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
  }

  public void refresh() {
    updateSupportTicketModelsRecyclerView();
  }

  private void updateSupportTicketModelsRecyclerView() {
    int itemCountMinusOne = supportTicketRecyclerViewAdapter.getItemCount() - 1;
    for (int i = itemCountMinusOne; i >= 0; i--) {
      supportTicketRecyclerViewAdapter.notifyItemRemoved(i);
    }

    supportTicketsModel.getGetSupportTicketModels().get();

    int pos = 0;
    for (SupportTicketModel unused : supportTicketsModel.getGetSupportTicketModels().get()) {
      supportTicketRecyclerViewAdapter.notifyItemInserted(pos);
      pos += 1;
    }
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
