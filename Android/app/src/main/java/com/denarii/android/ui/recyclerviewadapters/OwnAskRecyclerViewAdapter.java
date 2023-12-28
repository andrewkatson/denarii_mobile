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
import com.denarii.android.ui.models.OwnAsk;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class OwnAskRecyclerViewAdapter extends
        RecyclerView.Adapter<OwnAskRecyclerViewAdapter.ViewHolder> {

    private final List<OwnAsk> ownAsks;
    private final Function<Set<String>, Void> cancelAskIdsFunction;

    public OwnAskRecyclerViewAdapter(List<OwnAsk> ownAsks, Function<Set<String>, Void> cancelAskIdsFunction) {
        this.ownAsks = ownAsks;
        this.cancelAskIdsFunction = cancelAskIdsFunction;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public OwnAskRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.sell_denarii_own_asks_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(OwnAskRecyclerViewAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        OwnAsk ask = ownAsks.get(position);

        // Set item views based on your views and data model
        TextView amount = holder.amount;
        amount.setText(String.valueOf(ask.getAmount()));

        TextView price = holder.price;
        price.setText(String.valueOf(ask.getPrice()));

        Button cancel = holder.cancel;
        cancel.setOnClickListener(v -> {
            Set<String> askIdsToCancel = new HashSet<>();
            askIdsToCancel.add(ask.getAskId());

            cancelAskIdsFunction.apply(askIdsToCancel);
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return ownAsks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView amount;
        private final TextView price;

        private final Button cancel;

        public ViewHolder(View itemView) {
            super(itemView);

            amount = itemView.findViewById(R.id.ownAsksAmountItem);
            price = itemView.findViewById(R.id.ownAsksPriceItem);
            cancel = itemView.findViewById(R.id.ownAsksCancelBuyItem);
        }
    }
}
