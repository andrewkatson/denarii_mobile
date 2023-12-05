package com.denarii.android.activities.buydenarii;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.denarii.android.R;
import com.denarii.android.activities.creditcardinfo.CreditCardInfo;
import com.denarii.android.activities.openedwallet.OpenedWallet;
import com.denarii.android.activities.selldenarii.SellDenarii;
import com.denarii.android.activities.usersettings.UserSettings;
import com.denarii.android.activities.verification.Verification;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.ui.containers.DenariiAskArtifacts;
import com.denarii.android.user.DenariiAsk;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.UserDetails;
import com.denarii.android.util.DenariiServiceHandler;
import com.denarii.android.util.UnpackDenariiResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyDenarii extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

  private final Executor executor = Executors.newCachedThreadPool();
  private final ReentrantLock reentrantLock = new ReentrantLock();
  private final List<DenariiAsk> currentAsks = new ArrayList<>();
  private final List<DenariiAskArtifacts> currentAskArtifacts = new ArrayList<>();
  private List<DenariiAsk> queuedBuys = new ArrayList<>();
  private final List<DenariiAskArtifacts> queuedBuysArtifacts = new ArrayList<>();

  private DenariiService denariiService;

  private UserDetails userDetails = null;

  private static final Random random = new Random();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_buy_denarii);

    denariiService = DenariiServiceHandler.returnDenariiService();

    Intent currentIntent = getIntent();
    userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

    getNewAsks();
    refreshSettledTransactions();

    Button submit = (Button) findViewById(R.id.buy_denarii_submit);

    submit.setOnClickListener(
        v -> {
          onSubmitClicked();
        });
  }

  private void updateTables() {
    try {
      reentrantLock.lock();
      updateCurrentAsksTable();
      updateQueuedBuysTable();
    } finally {
      reentrantLock.unlock();
    }
  }

  private void updateCurrentAsksTable() {
    clearCurrentAsksTable();
    addAllCurrentAsksToTable();
  }

  private void clearCurrentAsksTable() {
    for (DenariiAskArtifacts artifacts : currentAskArtifacts) {
      artifacts.getAmountTextView().setVisibility(View.GONE);
      artifacts.getAskingPriceTextView().setVisibility(View.GONE);
    }

    currentAskArtifacts.clear();
  }

  private void addAllCurrentAsksToTable() {
    boolean firstArtifact = true;

    TextView amountLabel = findViewById(R.id.askTableAmount);
    TextView priceLabel = findViewById(R.id.priceAskTable);
    ConstraintLayout parentLayout = findViewById(R.id.buy_denarii_layout);
    TextView queuedBuysLabel = findViewById(R.id.queuedBuys);

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

      // If we are at the end of the list then constrain by the label below the last artifact
      if (i == currentAskArtifacts.size() - 1) {
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.BOTTOM, queuedBuysLabel.getId(), ConstraintSet.TOP);
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

      // If we are at the end of the list then constrain by the label below the last artifact
      if (i == currentAskArtifacts.size() - 1) {
        askingPriceConstraintSet.connect(
            askingPrice.getId(), ConstraintSet.BOTTOM, queuedBuysLabel.getId(), ConstraintSet.TOP);
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

  private void updateQueuedBuysTable() {
    clearQueuedBuysTable();
    addAllQueuedBuysToTable();
  }

  private void clearQueuedBuysTable() {
    for (DenariiAskArtifacts artifacts : queuedBuysArtifacts) {
      artifacts.getAmountTextView().setVisibility(View.GONE);
      artifacts.getAmountBoughtTextView().setVisibility(View.GONE);
      artifacts.getAskingPriceTextView().setVisibility(View.GONE);
      artifacts.getCancelButton().setVisibility(View.GONE);
    }

    queuedBuysArtifacts.clear();
  }

  private void addAllQueuedBuysToTable() {
    boolean firstArtifact = true;

    TextView amountLabel = findViewById(R.id.buyAmountTotal);
    TextView priceLabel = findViewById(R.id.buyPrice);
    TextView amountBoughtLabel = findViewById(R.id.buyAmountBought);
    TextView cancelLabel = findViewById(R.id.buyCancelBuy);
    ConstraintLayout parentLayout = findViewById(R.id.buy_denarii_layout);

    // First we make all the artifacts
    for (DenariiAsk ask : queuedBuys) {
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

      Button cancel = new Button(this);
      cancel.setId(random.nextInt(1000000));
      cancel.setText(R.string.cancel);
      cancel.setOnClickListener(
          v -> {
            Set<String> setOfOne = new HashSet<>();
            setOfOne.add(ask.getAskID());
            cancelBuys(setOfOne);
          });

      artifacts.setAmountTextView(amount);
      artifacts.setAskingPriceTextView(askingPrice);
      artifacts.setAmountBoughtTextView(amountBought);

      queuedBuysArtifacts.add(artifacts);
    }

    // Then we apply their constraints
    for (int i = 0; i < queuedBuysArtifacts.size(); i++) {
      DenariiAskArtifacts artifacts = queuedBuysArtifacts.get(i);
      TextView amount = artifacts.getAmountTextView();
      TextView askingPrice = artifacts.getAskingPriceTextView();
      TextView amountBought = artifacts.getAmountBoughtTextView();
      Button cancel = artifacts.getCancelButton();

      ConstraintSet amountConstraintSet = new ConstraintSet();
      amountConstraintSet.clone(parentLayout);
      amountConstraintSet.connect(
          amount.getId(), ConstraintSet.LEFT, parentLayout.getId(), ConstraintSet.LEFT);
      amountConstraintSet.connect(
          amount.getId(), ConstraintSet.RIGHT, askingPrice.getId(), ConstraintSet.LEFT);
      // If this is not the first artifact then we should connecct the top to the previous artifact
      if (!firstArtifact) {
        DenariiAskArtifacts previousArtifact = queuedBuysArtifacts.get(i - 1);
        TextView previousAmount = previousArtifact.getAmountTextView();
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.TOP, previousAmount.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise connect it to the label
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.TOP, amountLabel.getId(), ConstraintSet.BOTTOM);
      }

      // If we are at the end of the list then constrain by the label below the last artifact
      if (i == queuedBuysArtifacts.size() - 1) {
        amountConstraintSet.connect(
            amount.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiAskArtifacts nextArtifact = queuedBuysArtifacts.get(i + 1);
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
        DenariiAskArtifacts previousArtifact = queuedBuysArtifacts.get(i - 1);
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

      // If we are at the end of the list then constrain by the label below the last artifact
      if (i == queuedBuysArtifacts.size() - 1) {
        askingPriceConstraintSet.connect(
            askingPrice.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiAskArtifacts nextArtifact = queuedBuysArtifacts.get(i + 1);
        TextView nextAskingPrice = nextArtifact.getAskingPriceTextView();
        askingPriceConstraintSet.connect(
            askingPrice.getId(), ConstraintSet.BOTTOM, nextAskingPrice.getId(), ConstraintSet.TOP);
      }
      askingPriceConstraintSet.applyTo(parentLayout);

      ConstraintSet amountBoughtConstraint = new ConstraintSet();
      amountBoughtConstraint.clone(parentLayout);

      amountBoughtConstraint.connect(
          amountBought.getId(), ConstraintSet.RIGHT, cancel.getId(), ConstraintSet.LEFT);
      amountBoughtConstraint.connect(
          amountBought.getId(), ConstraintSet.LEFT, askingPrice.getId(), ConstraintSet.RIGHT);
      // If this is not the first artifact then we should connect the top to the previous artifact
      if (!firstArtifact) {
        DenariiAskArtifacts previousArtifact = queuedBuysArtifacts.get(i - 1);
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

      // If we are at the end of the list then constrain by the label below the last artifact
      if (i == queuedBuysArtifacts.size() - 1) {
        amountBoughtConstraint.connect(
            amountBought.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiAskArtifacts nextArtifact = queuedBuysArtifacts.get(i + 1);
        TextView nextAmountBought = nextArtifact.getAmountBoughtTextView();
        amountBoughtConstraint.connect(
            amountBought.getId(),
            ConstraintSet.BOTTOM,
            nextAmountBought.getId(),
            ConstraintSet.TOP);
      }
      amountBoughtConstraint.applyTo(parentLayout);

      ConstraintSet cancelConstraint = new ConstraintSet();
      cancelConstraint.clone(parentLayout);

      cancelConstraint.connect(
          cancel.getId(), ConstraintSet.RIGHT, parentLayout.getId(), ConstraintSet.RIGHT);
      cancelConstraint.connect(
          cancel.getId(), ConstraintSet.LEFT, amountBought.getId(), ConstraintSet.RIGHT);
      // If this is not the first artifact then we should connect the top to the previous artifact
      if (!firstArtifact) {
        DenariiAskArtifacts previousArtifact = queuedBuysArtifacts.get(i - 1);
        Button previousCancel = previousArtifact.getCancelButton();
        cancelConstraint.connect(
            cancel.getId(), ConstraintSet.TOP, previousCancel.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise connect it to the label
        cancelConstraint.connect(
            cancel.getId(), ConstraintSet.TOP, cancelLabel.getId(), ConstraintSet.BOTTOM);
      }

      // If we are at the end of the list then constrain by the label below the last artifact
      if (i == queuedBuysArtifacts.size() - 1) {
        cancelConstraint.connect(
            cancel.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiAskArtifacts nextArtifact = queuedBuysArtifacts.get(i + 1);
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

  @Override
  public void onRefresh() {
    getNewAsks();
    refreshSettledTransactions();
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

  private void refreshSettledTransactions() {
    try {
      reentrantLock.lock();

      if (Objects.equals(userDetails, null)) {
        userDetails = UnpackDenariiResponse.validUserDetails();
      }

      Set<String> askIdsToRemove = new HashSet<>();

      for (DenariiAsk ask : this.queuedBuys) {
        Call<List<DenariiResponse>> call =
            denariiService.isTransactionSettled(userDetails.getUserID(), ask.getAskID());

        call.enqueue(
            new Callback<>() {
              @Override
              public void onResponse(
                  @NonNull Call<List<DenariiResponse>> call,
                  @NonNull Response<List<DenariiResponse>> response) {
                if (response.isSuccessful()) {
                  if (response.body() != null) {
                    // We only care about the first wallet.
                    UnpackDenariiResponse.unpackIsTransactionSettled(
                        askIdsToRemove, response.body());
                  }
                }
              }

              @Override
              public void onFailure(
                  @NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
                // TODO log this
              }
            });
      }

      List<DenariiAsk> newQueuedBuys = new ArrayList<>();

      for (DenariiAsk buy : queuedBuys) {
        if (!askIdsToRemove.contains(buy.getAskID())) {
          newQueuedBuys.add(buy);
        }
      }

      queuedBuys = newQueuedBuys;
    } finally {
      reentrantLock.unlock();
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

        EditText amountEditText = findViewById(R.id.buy_denarii_amount);
        String amount = amountEditText.getText().toString();

        EditText priceEditText = findViewById(R.id.buy_denarii_price);
        String askingPrice = priceEditText.getText().toString();

        RadioButton trueBuyAnyPrice = findViewById(R.id.trueBuyAnyPrice);
        boolean buyRegardlessOfPrice = trueBuyAnyPrice.isChecked();

        RadioButton trueFailIfNotFull = findViewById(R.id.trueFailIfNotFull);
        boolean failIfFullAmountIsntMet = trueFailIfNotFull.isChecked();

        Call<List<DenariiResponse>> buyDenariiCall =
            denariiService.buyDenarii(
                finalDetails.getUserID(),
                amount,
                askingPrice,
                String.valueOf(buyRegardlessOfPrice),
                String.valueOf(failIfFullAmountIsntMet));

        List<DenariiAsk> newQueuedBuys = new ArrayList<>();

        final boolean[] buyDenariiSucceeded = {false};
        buyDenariiCall.enqueue(
            new Callback<>() {
              @Override
              public void onResponse(
                  @NonNull Call<List<DenariiResponse>> call,
                  @NonNull Response<List<DenariiResponse>> response) {
                if (response.isSuccessful()) {
                  if (response.body() != null) {
                    buyDenariiSucceeded[0] =
                        UnpackDenariiResponse.unpackBuyDenarii(newQueuedBuys, response.body());
                  }
                }
              }

              @Override
              public void onFailure(
                  @NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
                // TODO log this
              }
            });

        if (buyDenariiSucceeded[0]) {

          // TODO use other currencies
          Call<List<DenariiResponse>> getMoneyFromBuyerCall =
              denariiService.getMoneyFromBuyer(finalDetails.getUserID(), amount, "usd");

          final boolean[] getMoneyFromBuyerSucceeded = {false};
          getMoneyFromBuyerCall.enqueue(
              new Callback<>() {
                @Override
                public void onResponse(
                    @NonNull Call<List<DenariiResponse>> call,
                    @NonNull Response<List<DenariiResponse>> response) {
                  if (response.isSuccessful()) {
                    if (response.body() != null) {
                      getMoneyFromBuyerSucceeded[0] =
                          UnpackDenariiResponse.unpackGetMoneyFromBuyer(response.body());
                    }
                  }
                }

                @Override
                public void onFailure(
                    @NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
                  // TODO log this
                }
              });

          if (getMoneyFromBuyerSucceeded[0]) {

            List<DenariiAsk> succeededAsks = new ArrayList<>();
            boolean anyAskFailed = false;
            for (DenariiAsk ask : newQueuedBuys) {
              Call<List<DenariiResponse>> transferDenariiCall =
                  denariiService.transferDenarii(finalDetails.getUserID(), ask.getAskID());

              final boolean[] transferDenariiSucceeded = {false};
              final double[] amountBoughtTransfer = {0.0};
              transferDenariiCall.enqueue(
                  new Callback<>() {
                    @Override
                    public void onResponse(
                        @NonNull Call<List<DenariiResponse>> call,
                        @NonNull Response<List<DenariiResponse>> response) {
                      if (response.isSuccessful()) {
                        if (response.body() != null) {
                          transferDenariiSucceeded[0] =
                              UnpackDenariiResponse.unpackTransferDenarii(
                                  amountBoughtTransfer, response.body());
                        }
                      }
                    }

                    @Override
                    public void onFailure(
                        @NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
                      // TODO log this
                    }
                  });
              if (transferDenariiSucceeded[0]) {
                DenariiAsk currentAsk = getCurrentAsk(ask.getAskID());

                DenariiAsk newAsk = new DenariiAsk();
                newAsk.setAskID(ask.getAskID());
                newAsk.setAmount(currentAsk.getAmount());
                newAsk.setAskingPrice(currentAsk.getAskingPrice());
                newAsk.setAmountBought(amountBoughtTransfer[0]);

                queuedBuys.add(newAsk);
                succeededAsks.add(newAsk);
              } else {
                anyAskFailed = true;
                break;
              }
            }

            if (anyAskFailed) {
              createToast("Failed one of the transfers of denarii. Will refund money");
              reverseTransactions(succeededAsks);
            }
          } else {
            Set<String> askIdsToCancel =
                newQueuedBuys.stream().map(DenariiAsk::getAskID).collect(Collectors.toSet());
            cancelBuys(askIdsToCancel);
            createToast("Could not get money from your account");
          }
        } else {
          createToast("Could not buy denarii");
        }
      } else {
        createToast("Either missing verification or credit card info");
      }

    } finally {
      reentrantLock.unlock();
    }
  }

  private void cancelBuys(Set<String> askIdsToCancel) {
    Set<String> successfulCancellationAskIds = new HashSet<>();

    for (String askId : askIdsToCancel) {
      Call<List<DenariiResponse>> call =
          denariiService.cancelBuyOfAsk(userDetails.getUserID(), askId);

      final boolean[] succeeded = {false};
      call.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  succeeded[0] = UnpackDenariiResponse.unpackCancelBuyOfAsk(response.body());
                }
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
              // TODO log this
            }
          });

      if (succeeded[0]) {
        successfulCancellationAskIds.add(askId);
      } else {
        createToast(
            String.format(
                "Failed to cancel a buy: %s. Copy that down and file a support ticket", askId));
      }
    }

    List<DenariiAsk> newQueuedBuys = new ArrayList<>();

    for (DenariiAsk buy : queuedBuys) {
      if (!successfulCancellationAskIds.contains(buy.getAskID())) {
        newQueuedBuys.add(buy);
      }
    }

    queuedBuys = newQueuedBuys;
  }

  private void reverseTransactions(List<DenariiAsk> asks) {
    for (DenariiAsk askToReverse : asks) {
      // TODO do other currencies
      Call<List<DenariiResponse>> call =
          denariiService.sendMoneyBackToBuyer(
              userDetails.getUserID(), String.valueOf(askToReverse.getAmountBought()), "usd");

      final boolean[] succeeded = {false};
      call.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  succeeded[0] = UnpackDenariiResponse.unpackSendMoneyBackToBuyer(response.body());
                }
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
              // TODO log this
            }
          });

      if (succeeded[0]) {
        Set<String> hashSetOfOne = new HashSet<>();
        hashSetOfOne.add(askToReverse.getAskID());
        cancelBuys(hashSetOfOne);
      } else {
        createToast(
            String.format(
                "Failed to send money back to you for buy: %s. Copy that down and file a support ticket",
                askToReverse.getAskID()));
      }
    }
  }

  private DenariiAsk getCurrentAsk(String askId) {
    for (DenariiAsk ask : currentAsks) {
      if (Objects.equals(ask.getAskID(), askId)) {
        return ask;
      }
    }

    Call<List<DenariiResponse>> call =
        denariiService.getAskWithIdentifier(userDetails.getUserID(), askId);

    final DenariiAsk filledAsk = new DenariiAsk();

    call.enqueue(
        new Callback<>() {
          @Override
          public void onResponse(
              @NonNull Call<List<DenariiResponse>> call,
              @NonNull Response<List<DenariiResponse>> response) {
            if (response.isSuccessful()) {
              if (response.body() != null) {
                UnpackDenariiResponse.unpackGetAskWithIdentifier(filledAsk, response.body());
              }
            }
          }

          @Override
          public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
            // TODO log this
          }
        });

    return filledAsk;
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
      return true;
    } else if (Objects.equals(item.getItemId(), R.id.sell_denarii)) {
      Intent intent = new Intent(BuyDenarii.this, SellDenarii.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.wallet)) {
      Intent intent = new Intent(BuyDenarii.this, OpenedWallet.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.verification)) {
      Intent intent = new Intent(BuyDenarii.this, Verification.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.credit_card_info)) {
      Intent intent = new Intent(BuyDenarii.this, CreditCardInfo.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.settings)) {
      Intent intent = new Intent(BuyDenarii.this, UserSettings.class);

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
