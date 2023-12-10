package com.denarii.android.activities.selldenarii;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.denarii.android.R;
import com.denarii.android.activities.buydenarii.BuyDenarii;
import com.denarii.android.activities.creditcardinfo.CreditCardInfo;
import com.denarii.android.activities.openedwallet.OpenedWallet;
import com.denarii.android.activities.usersettings.UserSettings;
import com.denarii.android.activities.verification.Verification;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.ui.containers.DenariiAskArtifacts;
import com.denarii.android.user.DenariiAsk;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.UserDetails;
import com.denarii.android.util.UnpackDenariiResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellDenarii extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

  private final ReentrantLock reentrantLock = new ReentrantLock();
  private final List<DenariiAsk> currentAsks = new ArrayList<>();
  private final List<DenariiAskArtifacts> currentAskArtifacts = new ArrayList<>();
  private final List<DenariiAsk> ownAsks = new ArrayList<>();
  private final List<DenariiAskArtifacts> ownAsksArtifacts = new ArrayList<>();

  // Pending Sales
  private final List<DenariiAsk> ownAsksBought = new ArrayList<>();
  private final List<DenariiAskArtifacts> ownAsksBoughtArtifacts = new ArrayList<>();

  private DenariiService denariiService;

  private UserDetails userDetails = null;

  private static final Random random = new Random();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sell_denarii);

    Intent currentIntent = getIntent();
    userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

    Button submit = (Button) findViewById(R.id.submitSell);

    submit.setOnClickListener(
        v -> {
          onSubmitClicked();
        });

    getNewAsks();
    refreshOwnAsks();
    refreshAsksInEscrow();
    refreshGoingPrice();
  }

  @Override
  public void onRefresh() {
    getNewAsks();
    refreshOwnAsks();
    refreshAsksInEscrow();
    refreshGoingPrice();
    updateTables();
  }

  private void getNewAsks() {
    try {
      reentrantLock.lock();

      if (Objects.equals(userDetails, null)) {
        userDetails = UnpackDenariiResponse.validUserDetails();
      }

      currentAsks.clear();

      Call<List<DenariiResponse>> call = denariiService.getPrices(userDetails.getUserID());

      call.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  // We only care about the first wallet.
                  UnpackDenariiResponse.unpackGetPrices(currentAsks, response.body());
                }
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
              // TODO log this
            }
          });
    } finally {
      reentrantLock.unlock();
    }
  }

  private void refreshOwnAsks() {
    try {
      reentrantLock.lock();

      if (Objects.equals(userDetails, null)) {
        userDetails = UnpackDenariiResponse.validUserDetails();
      }

      ownAsks.clear();

      Call<List<DenariiResponse>> call = denariiService.getAllAsks(userDetails.getUserID());

      call.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  // We only care about the first wallet.
                  UnpackDenariiResponse.unpackGetAllAsks(ownAsks, response.body());
                }
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
              // TODO log this
            }
          });

    } finally {
      reentrantLock.unlock();
    }
  }

  private void refreshAsksInEscrow() {
    try {
      reentrantLock.lock();

      if (Objects.equals(userDetails, null)) {
        userDetails = UnpackDenariiResponse.validUserDetails();
      }

      ownAsksBought.clear();

      Call<List<DenariiResponse>> call =
          denariiService.pollForEscrowedTransaction(userDetails.getUserID());

      call.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  // We only care about the first wallet.
                  UnpackDenariiResponse.unpackPollForEscrowedTransaction(
                      ownAsksBought, response.body());
                }
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
              // TODO log this
            }
          });
    } finally {
      reentrantLock.unlock();
    }
  }

  private void refreshGoingPrice() {
    try {
      reentrantLock.lock();

      if (Objects.equals(userDetails, null)) {
        userDetails = UnpackDenariiResponse.validUserDetails();
      }

      double goingPrice = Double.MAX_VALUE;
      for (DenariiAsk ask : currentAsks) {
        if (goingPrice > ask.getAskingPrice()) {
          goingPrice = ask.getAskingPrice();
        }
      }

      TextView goingPriceTextView = findViewById(R.id.goingPrices);
      goingPriceTextView.setText(String.format("Going Price: %s", goingPrice));
    } finally {
      reentrantLock.unlock();
    }
  }

  private void updateTables() {
    try {
      reentrantLock.lock();
      updateCurrentAsksTable();
      updateOwnAsksTable();
      updatePendingSalesTable();
    } finally {
      reentrantLock.unlock();
    }
  }

  private void updateCurrentAsksTable() {
    clearCurrentAsksTable();
    addAllToCurrentAsksTable();
  }

  private void clearCurrentAsksTable() {
    for (DenariiAskArtifacts artifacts : currentAskArtifacts) {
      artifacts.getAmountTextView().setVisibility(View.GONE);
      artifacts.getAskingPriceTextView().setVisibility(View.GONE);
    }

    currentAskArtifacts.clear();
  }

  private void addAllToCurrentAsksTable() {
    boolean firstArtifact = true;

    TextView amountLabel = findViewById(R.id.sellAsksAmount);
    TextView priceLabel = findViewById(R.id.sellAsksPrice);
    ConstraintLayout parentLayout = findViewById(R.id.sellAsksLayout);

    // First we make all the artifacts
    for (DenariiAsk ask : currentAsks) {
      DenariiAskArtifacts artifacts = new DenariiAskArtifacts();

      TextView amount = new TextView(this);
      amount.setId(random.nextInt(1000000));
      amount.setText(String.valueOf(ask.getAmount()));

      TextView askingPrice = new TextView(this);
      askingPrice.setId(random.nextInt(1000000));
      askingPrice.setText(String.valueOf(ask.getAskingPrice()));

      artifacts.setAmountTextView(amount);
      artifacts.setAskingPriceTextView(askingPrice);

      currentAskArtifacts.add(artifacts);
    }

    // Then we apply their constraints
    for (int i = 0; i < currentAskArtifacts.size(); i++) {
      DenariiAskArtifacts artifacts = currentAskArtifacts.get(i);
      TextView amount = artifacts.getAmountTextView();
      TextView askingPrice = artifacts.getAskingPriceTextView();

      ConstraintSet amountConstraintSet = new ConstraintSet();
      amountConstraintSet.clone(parentLayout);
      amountConstraintSet.connect(
          amount.getId(), ConstraintSet.LEFT, parentLayout.getId(), ConstraintSet.LEFT);
      amountConstraintSet.connect(
          amount.getId(), ConstraintSet.RIGHT, askingPrice.getId(), ConstraintSet.LEFT);

      // If this is not the first artifact then we should connect the top to the previous artifact
      if (!firstArtifact) {
        DenariiAskArtifacts previousArtifact = currentAskArtifacts.get(i - 1);
        TextView previousAmount = previousArtifact.getAmountTextView();
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.TOP, previousAmount.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise connect it to the label
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.TOP, amountLabel.getId(), ConstraintSet.BOTTOM);
      }

      // If we are at the end of the list then constrain by the parent
      if (i == currentAskArtifacts.size() - 1) {
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiAskArtifacts nextArtifact = currentAskArtifacts.get(i + 1);
        TextView nextAmount = nextArtifact.getAmountTextView();
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.BOTTOM, nextAmount.getId(), ConstraintSet.TOP);
      }
      amountConstraintSet.applyTo(parentLayout);

      ConstraintSet askingPriceConstraintSet = new ConstraintSet();
      askingPriceConstraintSet.clone(parentLayout);

      askingPriceConstraintSet.connect(
          askingPrice.getId(), ConstraintSet.RIGHT, parentLayout.getId(), ConstraintSet.RIGHT);
      askingPriceConstraintSet.connect(
          askingPrice.getId(), ConstraintSet.LEFT, amount.getId(), ConstraintSet.RIGHT);
      // If this is not the first artifact then we should connect the top to the previous artifact
      if (!firstArtifact) {
        DenariiAskArtifacts previousArtifact = currentAskArtifacts.get(i - 1);
        TextView previousAskingPrice = previousArtifact.getAskingPriceTextView();
        askingPriceConstraintSet.connect(
            askingPrice.getId(),
            ConstraintSet.TOP,
            previousAskingPrice.getId(),
            ConstraintSet.BOTTOM);
      } else {
        // Otherwise connect it to the label
        askingPriceConstraintSet.connect(
            askingPrice.getId(), ConstraintSet.TOP, priceLabel.getId(), ConstraintSet.BOTTOM);
      }

      // If we are at the end of the list then constrain by the parent
      if (i == currentAskArtifacts.size() - 1) {
        askingPriceConstraintSet.connect(
            askingPrice.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiAskArtifacts nextArtifact = currentAskArtifacts.get(i + 1);
        TextView nextAskingPrice = nextArtifact.getAskingPriceTextView();
        askingPriceConstraintSet.connect(
            askingPrice.getId(), ConstraintSet.BOTTOM, nextAskingPrice.getId(), ConstraintSet.TOP);
      }
      askingPriceConstraintSet.applyTo(parentLayout);

      // The first artifact should help constrain the asking price and ask label for the pseudo
      // table
      if (firstArtifact) {
        ConstraintSet firstArtifactConstraintSet = new ConstraintSet();
        firstArtifactConstraintSet.clone(parentLayout);

        firstArtifactConstraintSet.connect(
            amountLabel.getId(), ConstraintSet.BOTTOM, amount.getId(), ConstraintSet.TOP);
        firstArtifactConstraintSet.connect(
            priceLabel.getId(), ConstraintSet.BOTTOM, askingPrice.getId(), ConstraintSet.TOP);

        firstArtifactConstraintSet.applyTo(parentLayout);
      }

      firstArtifact = false;
    }
  }

  private void updateOwnAsksTable() {
    clearOwnAsksTable();
    addAllToOwnAsksTable();
  }

  private void clearOwnAsksTable() {
    for (DenariiAskArtifacts artifacts : ownAsksArtifacts) {
      artifacts.getAmountTextView().setVisibility(View.GONE);
      artifacts.getAskingPriceTextView().setVisibility(View.GONE);
      artifacts.getAmountBoughtTextView().setVisibility(View.GONE);
      artifacts.getCancelButton().setVisibility(View.GONE);
    }

    ownAsksArtifacts.clear();
  }

  private void addAllToOwnAsksTable() {
    boolean firstArtifact = true;

    TextView amountLabel = findViewById(R.id.ownAsksAmount);
    TextView priceLabel = findViewById(R.id.ownAsksPrice);
    TextView cancelLabel = findViewById(R.id.ownAsksCancelBuy);
    ConstraintLayout parentLayout = findViewById(R.id.ownAsksLayout);

    // First we make all the artifacts
    for (DenariiAsk ask : ownAsks) {
      DenariiAskArtifacts artifacts = new DenariiAskArtifacts();

      TextView amount = new TextView(this);
      amount.setId(random.nextInt(1000000));
      amount.setText(String.valueOf(ask.getAmount()));

      TextView askingPrice = new TextView(this);
      askingPrice.setId(random.nextInt(1000000));
      askingPrice.setText(String.valueOf(ask.getAskingPrice()));

      Button cancel = new Button(this);
      cancel.setId(random.nextInt(1000000));
      cancel.setText(R.string.cancel);
      cancel.setOnClickListener(
          v -> {
            Set<String> setOfOne = new HashSet<>();
            setOfOne.add(ask.getAskID());
            cancelAsks(setOfOne);
          });

      artifacts.setAmountTextView(amount);
      artifacts.setAskingPriceTextView(askingPrice);
      artifacts.setCancelButton(cancel);

      ownAsksArtifacts.add(artifacts);
    }

    // Then we apply their constraints
    for (int i = 0; i < ownAsksArtifacts.size(); i++) {
      DenariiAskArtifacts artifacts = ownAsksArtifacts.get(i);
      TextView amount = artifacts.getAmountTextView();
      TextView askingPrice = artifacts.getAskingPriceTextView();
      Button cancel = artifacts.getCancelButton();

      ConstraintSet amountConstraintSet = new ConstraintSet();
      amountConstraintSet.clone(parentLayout);
      amountConstraintSet.connect(
          amount.getId(), ConstraintSet.LEFT, parentLayout.getId(), ConstraintSet.LEFT);
      amountConstraintSet.connect(
          amount.getId(), ConstraintSet.RIGHT, askingPrice.getId(), ConstraintSet.LEFT);
      // If this is not the first artifact then we should connect the top to the previous artifact
      if (!firstArtifact) {
        DenariiAskArtifacts previousArtifact = ownAsksArtifacts.get(i - 1);
        TextView previousAmount = previousArtifact.getAmountTextView();
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.TOP, previousAmount.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise connect it to the label
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.TOP, amountLabel.getId(), ConstraintSet.BOTTOM);
      }

      // If we are at the end of the list then constrain by the parent
      if (i == ownAsksArtifacts.size() - 1) {
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiAskArtifacts nextArtifact = ownAsksArtifacts.get(i + 1);
        TextView nextAmount = nextArtifact.getAmountTextView();
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.BOTTOM, nextAmount.getId(), ConstraintSet.TOP);
      }
      amountConstraintSet.applyTo(parentLayout);

      ConstraintSet askingPriceConstraintSet = new ConstraintSet();
      askingPriceConstraintSet.clone(parentLayout);

      askingPriceConstraintSet.connect(
          askingPrice.getId(), ConstraintSet.RIGHT, cancel.getId(), ConstraintSet.LEFT);
      askingPriceConstraintSet.connect(
          askingPrice.getId(), ConstraintSet.LEFT, amount.getId(), ConstraintSet.RIGHT);
      // If this is not the first artifact then we should connect the top to the previous artifact
      if (!firstArtifact) {
        DenariiAskArtifacts previousArtifact = ownAsksArtifacts.get(i - 1);
        TextView previousAskingPrice = previousArtifact.getAskingPriceTextView();
        askingPriceConstraintSet.connect(
            askingPrice.getId(),
            ConstraintSet.TOP,
            previousAskingPrice.getId(),
            ConstraintSet.BOTTOM);
      } else {
        // Otherwise connect it to the label
        askingPriceConstraintSet.connect(
            askingPrice.getId(), ConstraintSet.TOP, priceLabel.getId(), ConstraintSet.BOTTOM);
      }

      // If we are at the end of the list then constrain by the parent
      if (i == ownAsksArtifacts.size() - 1) {
        askingPriceConstraintSet.connect(
            askingPrice.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiAskArtifacts nextArtifact = ownAsksArtifacts.get(i + 1);
        TextView nextAskingPrice = nextArtifact.getAskingPriceTextView();
        askingPriceConstraintSet.connect(
            askingPrice.getId(), ConstraintSet.BOTTOM, nextAskingPrice.getId(), ConstraintSet.TOP);
      }
      askingPriceConstraintSet.applyTo(parentLayout);

      ConstraintSet cancelConstraint = new ConstraintSet();
      cancelConstraint.clone(parentLayout);

      cancelConstraint.connect(
          cancel.getId(), ConstraintSet.RIGHT, parentLayout.getId(), ConstraintSet.RIGHT);
      cancelConstraint.connect(
          cancel.getId(), ConstraintSet.LEFT, askingPrice.getId(), ConstraintSet.RIGHT);
      // If this is not the first artifact then we should connect the top to the previous artifact
      if (!firstArtifact) {
        DenariiAskArtifacts previousArtifact = ownAsksArtifacts.get(i - 1);
        Button previousCancel = previousArtifact.getCancelButton();
        cancelConstraint.connect(
            cancel.getId(), ConstraintSet.TOP, previousCancel.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise connect it to the label
        cancelConstraint.connect(
            cancel.getId(), ConstraintSet.TOP, cancelLabel.getId(), ConstraintSet.BOTTOM);
      }

      // If we are at the end of the list then constrain by the parent
      if (i == ownAsksArtifacts.size() - 1) {
        cancelConstraint.connect(
            cancel.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiAskArtifacts nextArtifact = ownAsksArtifacts.get(i + 1);
        TextView nextAmountBought = nextArtifact.getAmountBoughtTextView();
        cancelConstraint.connect(
            cancel.getId(), ConstraintSet.BOTTOM, nextAmountBought.getId(), ConstraintSet.TOP);
      }
      cancelConstraint.applyTo(parentLayout);

      // The first artifact should help constrain the asking price and ask label for the pseudo
      // table
      if (firstArtifact) {
        ConstraintSet firstArtifactConstraintSet = new ConstraintSet();
        firstArtifactConstraintSet.clone(parentLayout);

        firstArtifactConstraintSet.connect(
            amountLabel.getId(), ConstraintSet.BOTTOM, amount.getId(), ConstraintSet.TOP);
        firstArtifactConstraintSet.connect(
            priceLabel.getId(), ConstraintSet.BOTTOM, askingPrice.getId(), ConstraintSet.TOP);

        firstArtifactConstraintSet.applyTo(parentLayout);
      }

      firstArtifact = false;
    }
  }

  private void updatePendingSalesTable() {
    clearPendingSalesTable();
    addAllToPendingSalesTable();
  }

  private void clearPendingSalesTable() {

    for (DenariiAskArtifacts artifacts : ownAsksBoughtArtifacts) {
      artifacts.getAmountTextView().setVisibility(View.GONE);
      artifacts.getAskingPriceTextView().setVisibility(View.GONE);
      artifacts.getAmountBoughtTextView().setVisibility(View.GONE);
    }

    ownAsksBoughtArtifacts.clear();
  }

  private void addAllToPendingSalesTable() {
    boolean firstArtifact = true;

    TextView amountLabel = findViewById(R.id.pendingSalesAmount);
    TextView priceLabel = findViewById(R.id.pendingSalesPrice);
    TextView amountBoughtLabel = findViewById(R.id.pendingSalesAmountBought);
    ConstraintLayout parentLayout = findViewById(R.id.pendingSalesLayout);

    // First we make all the artifacts
    for (DenariiAsk ask : ownAsksBought) {
      DenariiAskArtifacts artifacts = new DenariiAskArtifacts();

      TextView amount = new TextView(this);
      amount.setId(random.nextInt(1000000));
      amount.setText(String.valueOf(ask.getAmount()));

      TextView askingPrice = new TextView(this);
      askingPrice.setId(random.nextInt(1000000));
      askingPrice.setText(String.valueOf(ask.getAskingPrice()));

      TextView amountBought = new TextView(this);
      amountBought.setId(random.nextInt(1000000));
      amountBought.setText(String.valueOf(ask.getAmountBought()));

      artifacts.setAmountTextView(amount);
      artifacts.setAskingPriceTextView(askingPrice);
      artifacts.setAmountBoughtTextView(amountBought);

      ownAsksBoughtArtifacts.add(artifacts);
    }

    // Then we apply their constraints
    for (int i = 0; i < ownAsksBoughtArtifacts.size(); i++) {
      DenariiAskArtifacts artifacts = ownAsksBoughtArtifacts.get(i);
      TextView amount = artifacts.getAmountTextView();
      TextView askingPrice = artifacts.getAskingPriceTextView();
      TextView amountBought = artifacts.getAmountBoughtTextView();

      ConstraintSet amountConstraintSet = new ConstraintSet();
      amountConstraintSet.clone(parentLayout);
      amountConstraintSet.connect(
          amount.getId(), ConstraintSet.LEFT, parentLayout.getId(), ConstraintSet.LEFT);
      amountConstraintSet.connect(
          amount.getId(), ConstraintSet.RIGHT, askingPrice.getId(), ConstraintSet.LEFT);
      // If this is not the first artifact then we should connecct the top to the previous artifact
      if (!firstArtifact) {
        DenariiAskArtifacts previousArtifact = ownAsksBoughtArtifacts.get(i - 1);
        TextView previousAmount = previousArtifact.getAmountTextView();
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.TOP, previousAmount.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise connect it to the label
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.TOP, amountLabel.getId(), ConstraintSet.BOTTOM);
      }

      // If we are at the end of the list then constrain by the parent
      if (i == ownAsksBoughtArtifacts.size() - 1) {
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiAskArtifacts nextArtifact = ownAsksBoughtArtifacts.get(i + 1);
        TextView nextAmount = nextArtifact.getAmountTextView();
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.BOTTOM, nextAmount.getId(), ConstraintSet.TOP);
      }
      amountConstraintSet.applyTo(parentLayout);

      ConstraintSet askingPriceConstraintSet = new ConstraintSet();
      askingPriceConstraintSet.clone(parentLayout);

      askingPriceConstraintSet.connect(
          askingPrice.getId(), ConstraintSet.RIGHT, amountBought.getId(), ConstraintSet.LEFT);
      askingPriceConstraintSet.connect(
          askingPrice.getId(), ConstraintSet.LEFT, amount.getId(), ConstraintSet.RIGHT);
      // If this is not the first artifact then we should connect the top to the previous artifact
      if (!firstArtifact) {
        DenariiAskArtifacts previousArtifact = ownAsksBoughtArtifacts.get(i - 1);
        TextView previousAskingPrice = previousArtifact.getAskingPriceTextView();
        askingPriceConstraintSet.connect(
            askingPrice.getId(),
            ConstraintSet.TOP,
            previousAskingPrice.getId(),
            ConstraintSet.BOTTOM);
      } else {
        // Otherwise connect it to the label
        askingPriceConstraintSet.connect(
            askingPrice.getId(), ConstraintSet.TOP, priceLabel.getId(), ConstraintSet.BOTTOM);
      }

      // If we are at the end of the list then constrain by the parent
      if (i == ownAsksBoughtArtifacts.size() - 1) {
        askingPriceConstraintSet.connect(
            askingPrice.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiAskArtifacts nextArtifact = ownAsksBoughtArtifacts.get(i + 1);
        TextView nextAskingPrice = nextArtifact.getAskingPriceTextView();
        askingPriceConstraintSet.connect(
            askingPrice.getId(), ConstraintSet.BOTTOM, nextAskingPrice.getId(), ConstraintSet.TOP);
      }
      askingPriceConstraintSet.applyTo(parentLayout);

      ConstraintSet amountBoughtConstraint = new ConstraintSet();
      amountBoughtConstraint.clone(parentLayout);

      amountBoughtConstraint.connect(
          amountBought.getId(), ConstraintSet.RIGHT, parentLayout.getId(), ConstraintSet.RIGHT);
      amountBoughtConstraint.connect(
          amountBought.getId(), ConstraintSet.LEFT, askingPrice.getId(), ConstraintSet.RIGHT);
      // If this is not the first artifact then we should connect the top to the previous artifact
      if (!firstArtifact) {
        DenariiAskArtifacts previousArtifact = ownAsksBoughtArtifacts.get(i - 1);
        TextView previousAmountBought = previousArtifact.getAmountBoughtTextView();
        amountBoughtConstraint.connect(
            amountBought.getId(),
            ConstraintSet.TOP,
            previousAmountBought.getId(),
            ConstraintSet.BOTTOM);
      } else {
        // Otherwise connect it to the label
        amountBoughtConstraint.connect(
            amountBought.getId(),
            ConstraintSet.TOP,
            amountBoughtLabel.getId(),
            ConstraintSet.BOTTOM);
      }

      // If we are at the end of the list then constrain by the parent
      if (i == ownAsksBoughtArtifacts.size() - 1) {
        amountBoughtConstraint.connect(
            amountBought.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiAskArtifacts nextArtifact = ownAsksBoughtArtifacts.get(i + 1);
        TextView nextAmountBought = nextArtifact.getAmountBoughtTextView();
        amountBoughtConstraint.connect(
            amountBought.getId(),
            ConstraintSet.BOTTOM,
            nextAmountBought.getId(),
            ConstraintSet.TOP);
      }
      amountBoughtConstraint.applyTo(parentLayout);

      // The first artifact should help constrain the asking price and ask label for the pseudo
      // table
      if (firstArtifact) {
        ConstraintSet firstArtifactConstraintSet = new ConstraintSet();
        firstArtifactConstraintSet.clone(parentLayout);

        firstArtifactConstraintSet.connect(
            amountLabel.getId(), ConstraintSet.BOTTOM, amount.getId(), ConstraintSet.TOP);
        firstArtifactConstraintSet.connect(
            priceLabel.getId(), ConstraintSet.BOTTOM, askingPrice.getId(), ConstraintSet.TOP);

        firstArtifactConstraintSet.applyTo(parentLayout);
      }

      firstArtifact = false;
    }
  }

  private void onSubmitClicked() {
    try {
      reentrantLock.lock();

      if (Objects.equals(userDetails, null)) {
        userDetails = UnpackDenariiResponse.validUserDetails();
      }

      final UserDetails finalDetails = userDetails;

      Call<List<DenariiResponse>> hasCreditCardInfoCall =
          denariiService.hasCreditCardInfo(userDetails.getUserID());

      hasCreditCardInfoCall.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  UnpackDenariiResponse.unpackHasCreditCardInfo(finalDetails, response.body());
                }
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
              // TODO log this
            }
          });

      Call<List<DenariiResponse>> isVerifiedCall =
          denariiService.isAVerifiedPerson(userDetails.getUserID());

      isVerifiedCall.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  UnpackDenariiResponse.unpackIsAVerifiedPerson(finalDetails, response.body());
                }
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
              // TODO log this
            }
          });

      if (finalDetails.getDenariiUser().getIsVerified()
          && finalDetails.getCreditCard().getHasCreditCardInfo()) {

        EditText amountEditText = findViewById(R.id.sellAmount);
        String amount = amountEditText.getText().toString();

        EditText priceEditText = findViewById(R.id.sellPrice);
        String askingPrice = priceEditText.getText().toString();

        Call<List<DenariiResponse>> makeDenariiAskCall =
            denariiService.makeDenariiAsk(userDetails.getUserID(), amount, askingPrice);

        final boolean[] sellDenariiSucceeded = {false};
        makeDenariiAskCall.enqueue(
            new Callback<>() {
              @Override
              public void onResponse(
                  @NonNull Call<List<DenariiResponse>> call,
                  @NonNull Response<List<DenariiResponse>> response) {
                if (response.isSuccessful()) {
                  if (response.body() != null) {
                    sellDenariiSucceeded[0] =
                        UnpackDenariiResponse.unpackMakeDenariiAsk(response.body());
                  }
                }
              }

              @Override
              public void onFailure(
                  @NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
                // TODO log this
              }
            });
        if (sellDenariiSucceeded[0]) {
          createToast("Successfully made denarii ask");
        } else {
          createToast("Failed to make denarii ask");
        }
      } else {
        createToast("Either missing verification or credit card info");
      }
    } finally {
      reentrantLock.unlock();
    }
  }

  private void cancelAsks(Set<String> askIdsToCancel) {
    for (String askId : askIdsToCancel) {
      Call<List<DenariiResponse>> makeDenariiAskCall =
          denariiService.cancelAsk(userDetails.getUserID(), askId);

      final boolean[] succeeded = {false};
      makeDenariiAskCall.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  succeeded[0] = UnpackDenariiResponse.unpackCancelAsk(response.body());
                }
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
              // TODO log this
            }
          });

      if (succeeded[0]) {
        createToast("Cancelled ask!");
      } else {
        createToast("Failed to cancel ask");
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (Objects.equals(userDetails, null)) {
      return true;
    }

    // Handle item selection.
    if (Objects.equals(item.getItemId(), R.id.buy_denarii)) {
      Intent intent = new Intent(SellDenarii.this, BuyDenarii.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.sell_denarii)) {

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.wallet)) {
      Intent intent = new Intent(SellDenarii.this, OpenedWallet.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.verification)) {
      Intent intent = new Intent(SellDenarii.this, Verification.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.credit_card_info)) {
      Intent intent = new Intent(SellDenarii.this, CreditCardInfo.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.settings)) {
      Intent intent = new Intent(SellDenarii.this, UserSettings.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  private void createToast(String message) {
    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, message, duration);
    toast.show();
  }
}
