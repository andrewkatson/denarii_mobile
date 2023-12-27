package com.denarii.android.ui.recyclerviewadapters;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.denarii.android.R;

public class QueuedBuyRecyclerViewAdapter extends
        RecyclerView.Adapter<QueuedBuyRecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView amount;
        private TextView price;

        private TextView amountBought;

        private Button cancel;

        public ViewHolder(View itemView) {
            super(itemView);

            amount = itemView.findViewById(R.id.buyAmountTotalItem);
            price = itemView.findViewById(R.id.buyPriceItem);
            amountBought = itemView.findViewById(R.id.buyAmountBoughtItem);
            cancel = itemView.findViewById(R.id.buyCancelBuyItem);
        }
    }
}
