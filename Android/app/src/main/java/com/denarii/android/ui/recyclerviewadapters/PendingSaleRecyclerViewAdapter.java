package com.denarii.android.ui.recyclerviewadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denarii.android.R;
import com.denarii.android.ui.models.PendingSale;

import java.util.List;

public class PendingSaleRecyclerViewAdapter extends
        RecyclerView.Adapter<PendingSaleRecyclerViewAdapter.ViewHolder> {

    private final List<PendingSale> pendingSales;

    public PendingSaleRecyclerViewAdapter(List<PendingSale> pendingSales) {
        this.pendingSales = pendingSales;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public PendingSaleRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.sell_denarii_pending_sales_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PendingSaleRecyclerViewAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        PendingSale ask = pendingSales.get(position);

        // Set item views based on your views and data model
        TextView amount = holder.amount;
        amount.setText(String.valueOf(ask.getAmount()));

        TextView price = holder.price;
        price.setText(String.valueOf(ask.getPrice()));

        TextView amountBought = holder.amountBought;
        amountBought.setText(String.valueOf(ask.getAmountBought()));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return pendingSales.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView amount;
        private final TextView price;

        private final TextView amountBought;

        public ViewHolder(View itemView) {
            super(itemView);

            amount = itemView.findViewById(R.id.pendingSalesAmountItem);
            price = itemView.findViewById(R.id.pendingSalesPriceItem);
            amountBought = itemView.findViewById(R.id.pendingSalesAmountBoughtItem);
        }
    }
}
