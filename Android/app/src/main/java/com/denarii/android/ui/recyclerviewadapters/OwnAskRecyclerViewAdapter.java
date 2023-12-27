package com.denarii.android.ui.recyclerviewadapters;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.denarii.android.R;

public class OwnAskRecyclerViewAdapter extends
        RecyclerView.Adapter<OwnAskRecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView amount;
        private TextView price;

        private Button cancel;

        public ViewHolder(View itemView) {
            super(itemView);

            amount = itemView.findViewById(R.id.ownAsksAmountItem);
            price = itemView.findViewById(R.id.ownAsksPriceItem);
            cancel = itemView.findViewById(R.id.ownAsksCancelBuyItem)
        }
    }
}
