package com.denarii.android.ui.recyclerviewadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.denarii.android.R;
import com.denarii.android.ui.models.Ask;
import com.denarii.android.ui.models.OwnAsk;
import com.denarii.android.ui.models.PendingSale;
import com.denarii.android.ui.models.SellDenariiModel;

public class SellDenariiRecyclerViewAdapter
    extends RecyclerView.Adapter<SellDenariiRecyclerViewAdapter.ViewHolder> {

  private final SellDenariiModel sellDenariiModel;

  private SellDenariiAskRecyclerViewAdapter asksRecyclerViewAdapter = null;
  private OwnAskRecyclerViewAdapter ownAsksRecyclerViewAdapter = null;
  private PendingSaleRecyclerViewAdapter pendingSalesRecyclerViewAdapter = null;

  private ViewHolder viewHolder = null;

  public SellDenariiRecyclerViewAdapter(SellDenariiModel sellDenariiModel) {
    this.sellDenariiModel = sellDenariiModel;
  }

  // Usually involves inflating a layout from XML and returning the holder
  @NonNull
  @Override
  public SellDenariiRecyclerViewAdapter.ViewHolder onCreateViewHolder(
      ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    // Inflate the custom layout
    View contactView = inflater.inflate(R.layout.sell_denarii_interior, parent, false);

    // Return a new holder instance
    viewHolder = new SellDenariiRecyclerViewAdapter.ViewHolder(contactView);
    return viewHolder;
  }

  // Involves populating data into the item through holder
  @Override
  public void onBindViewHolder(
      @NonNull SellDenariiRecyclerViewAdapter.ViewHolder holder, int position) {
    Button submit = (Button) holder.itemView.findViewById(R.id.submitSell);

    submit.setOnClickListener(
        v -> {
          sellDenariiModel.getClickSubmit().get();
        });

    // Create the recycler view for the asks grid
    RecyclerView asksRecyclerView =
        (RecyclerView) holder.itemView.findViewById(R.id.sellDenariiAsksRecyclerView);

    asksRecyclerViewAdapter =
        new SellDenariiAskRecyclerViewAdapter(sellDenariiModel.getGetAsks().get());
    asksRecyclerView.setAdapter(asksRecyclerViewAdapter);
    asksRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

    // Create the recycler view for the own asks grid
    RecyclerView ownAsksRecyclerView =
        (RecyclerView) holder.itemView.findViewById(R.id.sellDenariiOwnAsksRecyclerView);

    ownAsksRecyclerViewAdapter =
        new OwnAskRecyclerViewAdapter(
            sellDenariiModel.getGetOwnAsks().get(),
            (askIds) -> {
              sellDenariiModel.getCancelAsks().apply(askIds);
              return null;
            });
    ownAsksRecyclerView.setAdapter(ownAsksRecyclerViewAdapter);
    ownAsksRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

    // Create the recycler view for the pending sales grid
    RecyclerView pendingSalesRecyclerView =
        (RecyclerView) holder.itemView.findViewById(R.id.sellDenariiPendingSalesRecyclerView);

    pendingSalesRecyclerViewAdapter =
        new PendingSaleRecyclerViewAdapter(sellDenariiModel.getGetPendingSales().get());
    pendingSalesRecyclerView.setAdapter(pendingSalesRecyclerViewAdapter);
    pendingSalesRecyclerView.setLayoutManager(
        new LinearLayoutManager(holder.itemView.getContext()));

    sellDenariiModel.getSetGoingPrice().apply(holder);
  }

  public void refresh() {
    updateAsksRecyclerView();
    updateOwnAsksRecyclerView();
    updatePendingSalesRecyclerView();

    sellDenariiModel.getSetGoingPrice().apply(viewHolder);
  }

  private void updateAsksRecyclerView() {
    int itemCountMinusOne = asksRecyclerViewAdapter.getItemCount() - 1;
    for (int i = itemCountMinusOne; i >= 0; i--) {
      asksRecyclerViewAdapter.notifyItemRemoved(i);
    }

    sellDenariiModel.getGetCurrentAsks().get();

    int pos = 0;
    for (Ask unused : sellDenariiModel.getGetAsks().get()) {
      asksRecyclerViewAdapter.notifyItemInserted(pos);
      pos += 1;
    }
  }

  private void updateOwnAsksRecyclerView() {
    int itemCountMinusOne = ownAsksRecyclerViewAdapter.getItemCount() - 1;
    for (int i = itemCountMinusOne; i >= 0; i--) {
      ownAsksRecyclerViewAdapter.notifyItemRemoved(i);
    }

    sellDenariiModel.getGetOwnDenariiAsks().get();

    int pos = 0;
    for (OwnAsk unused : sellDenariiModel.getGetOwnAsks().get()) {
      ownAsksRecyclerViewAdapter.notifyItemInserted(pos);
      pos += 1;
    }
  }

  private void updatePendingSalesRecyclerView() {
    int itemCountMinusOne = pendingSalesRecyclerViewAdapter.getItemCount() - 1;
    for (int i = itemCountMinusOne; i >= 0; i--) {
      pendingSalesRecyclerViewAdapter.notifyItemRemoved(i);
    }

    sellDenariiModel.getGetPendingSalesOfDenariiAsks().get();

    int pos = 0;
    for (PendingSale unused : sellDenariiModel.getGetPendingSales().get()) {
      pendingSalesRecyclerViewAdapter.notifyItemInserted(pos);
      pos += 1;
    }
  }

  public void setGoingPrice(String goingPrice, ViewHolder viewHolder) {
    TextView goingPriceTextView = viewHolder.itemView.findViewById(R.id.goingPrices);
    goingPriceTextView.setText(String.format("Going Price: %s", goingPrice));
  }

  // Returns the total count of items in the list
  @Override
  public int getItemCount() {
    return 1;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(View itemView) {
      super(itemView);
    }
  }
}
