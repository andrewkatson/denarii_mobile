package com.denarii.android.ui.recyclerviewadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denarii.android.R;
import com.denarii.android.ui.models.SupportTicketModel;

import java.util.List;
import java.util.function.Function;

public class SupportTicketRecyclerViewAdapter extends
        RecyclerView.Adapter<SupportTicketRecyclerViewAdapter.ViewHolder> {

    private final List<SupportTicketModel> supportTickets;

    private final Function<String, Void> navigateToSupportTicketDetailsFunction;

    public SupportTicketRecyclerViewAdapter(List<SupportTicketModel> supportTickets, Function<String, Void> navigateToSupportTicketDetailsFunction) {
        this.supportTickets = supportTickets;
        this.navigateToSupportTicketDetailsFunction = navigateToSupportTicketDetailsFunction;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public SupportTicketRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.support_tickets_support_tickets_item, parent, false);

        // Return a new holder instance
        return new SupportTicketRecyclerViewAdapter.ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(SupportTicketRecyclerViewAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        SupportTicketModel ticket = supportTickets.get(position);

        // Set item views based on your views and data model
        Button navigate = holder.navigate;
        navigate.setText(ticket.getTitle());
        navigate.setOnClickListener(v -> {
            navigateToSupportTicketDetailsFunction.apply(ticket.getSupportTicketId());
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return supportTickets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final Button navigate;

        public ViewHolder(View itemView) {
            super(itemView);

            navigate = itemView.findViewById(R.id.support_tickets_navigate_to_ticket_button);
        }
    }
}
