package com.denarii.android.ui.recyclerviewadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denarii.android.R;
import com.denarii.android.ui.models.QueuedBuy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class QueuedBuyRecyclerViewAdapter extends
        RecyclerView.Adapter<QueuedBuyRecyclerViewAdapter.ViewHolder> {

    private final List<QueuedBuy> queuedBuys;
    private final Function<Set<String>, Void> askIdsToCancelFunction;

    public QueuedBuyRecyclerViewAdapter(List<QueuedBuy> queuedBuys, Function<Set<String>, Void> askIdsToCancelFunction) {
        this.queuedBuys = queuedBuys;
        this.askIdsToCancelFunction = askIdsToCancelFunction;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public QueuedBuyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.buy_denarii_queued_buys_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(QueuedBuyRecyclerViewAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        QueuedBuy ask = queuedBuys.get(position);

        // Set item views based on your views and data model
        TextView amount = holder.amount;
        amount.setText(String.valueOf(ask.getAmount()));

        TextView price = holder.price;
        price.setText(String.valueOf(ask.getPrice()));

        TextView amountBought = holder.amountBought;
        amountBought.setText(String.valueOf(ask.getAmountBought()));

        Button cancel = holder.cancel;
        cancel.setText(R.string.cancel);
        cancel.setOnClickListener(v -> {
            Set<String> askIdsToCancel = new HashSet<>();
            askIdsToCancel.add(ask.getAskId());

            askIdsToCancelFunction.apply(askIdsToCancel);
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return queuedBuys.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView amount;
        private final TextView price;
        private final TextView amountBought;
        private final Button cancel;

        public ViewHolder(View itemView) {
            super(itemView);

            amount = itemView.findViewById(R.id.buyAmountTotalItem);
            price = itemView.findViewById(R.id.buyPriceItem);
            amountBought = itemView.findViewById(R.id.buyAmountBoughtItem);
            cancel = itemView.findViewById(R.id.buyCancelBuyItem);
        }
    }
}
