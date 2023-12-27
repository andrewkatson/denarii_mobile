package com.denarii.android.ui.recyclerviewadapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.denarii.android.R;

public class PendingSaleRecyclerViewAdapter extends
        RecyclerView.Adapter<PendingSaleRecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView amount;
        private TextView price;

        private TextView amountBought;

        public ViewHolder(View itemView) {
            super(itemView);

            amount = itemView.findViewById(R.id.pendingSalesAmountItem);
            price = itemView.findViewById(R.id.pendingSalesPriceItem);
            amountBought = itemView.findViewById(R.id.pendingSalesAmountBoughtItem)
        }
    }
}
