package com.denarii.android.ui.recyclerviewadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.denarii.android.R;
import com.denarii.android.ui.models.Ask;
import com.denarii.android.ui.models.BuyDenariiModel;
import com.denarii.android.ui.models.QueuedBuy;
import java.util.function.Supplier;

public class BuyDenariiRecyclerViewAdapter
    extends RecyclerView.Adapter<BuyDenariiRecyclerViewAdapter.ViewHolder> {

  private final BuyDenariiModel buyDenariiModel;

  private BuyDenariiAskRecyclerViewAdapter asksRecyclerViewAdapter;

  private QueuedBuyRecyclerViewAdapter queuedBuyRecyclerViewAdapter;

  public BuyDenariiRecyclerViewAdapter(BuyDenariiModel buyDenariiModel) {
    this.buyDenariiModel = buyDenariiModel;
  }

  // Usually involves inflating a layout from XML and returning the holder
  @NonNull
  @Override
  public BuyDenariiRecyclerViewAdapter.ViewHolder onCreateViewHolder(
      ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    // Inflate the custom layout
    View contactView = inflater.inflate(R.layout.buy_denarii_interior, parent, false);

    // Return a new holder instance
    return new BuyDenariiRecyclerViewAdapter.ViewHolder(contactView);
  }

  // Involves populating data into the item through holder
  @Override
  public void onBindViewHolder(
      @NonNull BuyDenariiRecyclerViewAdapter.ViewHolder holder, int position) {

    Button submit = (Button) holder.itemView.findViewById(R.id.buy_denarii_submit);
    submit.setOnClickListener(
        v -> {
          Supplier<Void> clickSubmit = buyDenariiModel.getClickSubmit();
          clickSubmit.get();
        });

    // Create the recycler view for the asks grid
    RecyclerView asksRecyclerView =
        (RecyclerView) holder.itemView.findViewById(R.id.buyDenariiAsksRecyclerView);

    asksRecyclerViewAdapter =
        new BuyDenariiAskRecyclerViewAdapter(buyDenariiModel.getGetAsks().get());
    asksRecyclerView.setAdapter(asksRecyclerViewAdapter);
    asksRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

    // Create the recycler view for the queued buys grid
    RecyclerView queuedBuysRecyclerView =
        (RecyclerView) holder.itemView.findViewById(R.id.buyDenariiQueuedBuysRecyclerView);

    queuedBuyRecyclerViewAdapter =
        new QueuedBuyRecyclerViewAdapter(
            buyDenariiModel.getGetQueuedBuys().get(),
            (askIds) -> {
              buyDenariiModel.getCancelBuys().apply(askIds);
              return null;
            });
    queuedBuysRecyclerView.setAdapter(queuedBuyRecyclerViewAdapter);
    queuedBuysRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
  }

  // Returns the total count of items in the list
  @Override
  public int getItemCount() {
    return 1;
  }

  public void refresh() {
    updateAsksRecyclerView();
    updateQueuedBuysRecyclerView();
  }

  private void updateAsksRecyclerView() {
    int itemCountMinusOne = asksRecyclerViewAdapter.getItemCount() - 1;
    for (int i = itemCountMinusOne; i >= 0; i--) {
      asksRecyclerViewAdapter.notifyItemRemoved(i);
    }

    buyDenariiModel.getGetCurrentAsks().get();

    int pos = 0;
    for (Ask unused : buyDenariiModel.getGetAsks().get()) {
      asksRecyclerViewAdapter.notifyItemInserted(pos);
      pos += 1;
    }
  }

  private void updateQueuedBuysRecyclerView() {
    int itemCountMinusOne = queuedBuyRecyclerViewAdapter.getItemCount() - 1;
    for (int i = itemCountMinusOne; i >= 0; i--) {
      queuedBuyRecyclerViewAdapter.notifyItemRemoved(i);
    }

    buyDenariiModel.getGetQueuedDenariiAskBuys().get();

    int pos = 0;
    for (QueuedBuy unused : buyDenariiModel.getGetQueuedBuys().get()) {
      queuedBuyRecyclerViewAdapter.notifyItemInserted(pos);
      pos += 1;
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(View itemView) {
      super(itemView);
    }
  }
}
