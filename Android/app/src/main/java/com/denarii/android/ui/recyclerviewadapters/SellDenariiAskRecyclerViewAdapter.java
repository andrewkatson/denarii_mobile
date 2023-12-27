package com.denarii.android.ui.recyclerviewadapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.denarii.android.R;

public class SellDenariiAskRecyclerViewAdapter extends
        RecyclerView.Adapter<SellDenariiAskRecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView amount;
        private TextView price;

        public ViewHolder(View itemView) {
            super(itemView);

            amount = itemView.findViewById(R.id.sellAsksAmountItem);
            price = itemView.findViewById(R.id.sellAsksPriceItem);
        }
    }
}
