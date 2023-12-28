package com.denarii.android.ui.recyclerviewadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denarii.android.R;
import com.denarii.android.ui.models.Ask;

import java.util.List;

public class SellDenariiAskRecyclerViewAdapter extends
        RecyclerView.Adapter<SellDenariiAskRecyclerViewAdapter.ViewHolder> {

    private final List<Ask> asks;

    public SellDenariiAskRecyclerViewAdapter(List<Ask> asks) {
        this.asks = asks;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public SellDenariiAskRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.sell_denarii_asks_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(SellDenariiAskRecyclerViewAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Ask ask = asks.get(position);

        // Set item views based on your views and data model
        TextView amount = holder.amount;
        amount.setText(String.valueOf(ask.getAmount()));

        TextView price = holder.price;
        price.setText(String.valueOf(ask.getPrice()));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return asks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView amount;
        private final TextView price;

        public ViewHolder(View itemView) {
            super(itemView);

            amount = itemView.findViewById(R.id.sellAsksAmountItem);
            price = itemView.findViewById(R.id.sellAsksPriceItem);
        }
    }
}
