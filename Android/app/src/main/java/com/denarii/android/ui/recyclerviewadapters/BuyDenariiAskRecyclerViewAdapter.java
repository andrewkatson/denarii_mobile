package com.denarii.android.ui.recyclerviewadapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.denarii.android.R;

public class BuyDenariiAskRecyclerViewAdapter extends
        RecyclerView.Adapter<BuyDenariiAskRecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView amount;
        private TextView price;

        public ViewHolder(View itemView) {
            super(itemView);

            amount = itemView.findViewById(com.denarii.android.R.id.askTableAmountItem);
            price = itemView.findViewById(R.id.priceAskTableItem);
        }
    }
}
