package com.denarii.android.ui.recyclerviewadapters;

import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.denarii.android.R;

public class SupportTicketRecyclerViewAdapter extends
        RecyclerView.Adapter<SupportTicketRecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Button navigate;

        public ViewHolder(View itemView) {
            super(itemView);

            navigate = itemView.findViewById(R.id.support_tickets_navigate_to_ticket_button);
        }
    }
}
