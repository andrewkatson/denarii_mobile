package com.denarii.android.activities.selldenarii;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.denarii.android.R;
import com.denarii.android.activities.buydenarii.BuyDenarii;
import com.denarii.android.activities.creditcardinfo.CreditCardInfo;
import com.denarii.android.activities.openedwallet.OpenedWallet;
import com.denarii.android.activities.usersettings.UserSettings;
import com.denarii.android.activities.verification.Verification;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.ui.models.Ask;
import com.denarii.android.ui.models.OwnAsk;
import com.denarii.android.ui.models.PendingSale;
import com.denarii.android.ui.recyclerviewadapters.OwnAskRecyclerViewAdapter;
import com.denarii.android.ui.recyclerviewadapters.PendingSaleRecyclerViewAdapter;
import com.denarii.android.ui.recyclerviewadapters.SellDenariiAskRecyclerViewAdapter;
import com.denarii.android.user.DenariiAsk;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.UserDetails;
import com.denarii.android.util.DenariiServiceHandler;
import com.denarii.android.util.UnpackDenariiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellDenarii extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final List<DenariiAsk> currentAsks = new ArrayList<>();
    private final List<DenariiAsk> ownAsks = new ArrayList<>();

    // Pending Sales
    private final List<DenariiAsk> ownAsksBought = new ArrayList<>();

    private DenariiService denariiService;

    private UserDetails userDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_denarii);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.sell_denarii_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        Intent currentIntent = getIntent();
        userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

        denariiService = DenariiServiceHandler.returnDenariiService();

        Button submit = (Button) findViewById(R.id.submitSell);

        submit.setOnClickListener(
                v -> {
                    onSubmitClicked();
                });

        getNewAsks();
        refreshOwnAsks();
        refreshAsksInEscrow();
        refreshGoingPrice();

        // Create the recycler view for the asks grid
        RecyclerView asksRecyclerView = (RecyclerView) findViewById(R.id.sellDenariiAsksRecyclerView);

        SellDenariiAskRecyclerViewAdapter asksAdapter = new SellDenariiAskRecyclerViewAdapter(getCurrentAsks());
        asksRecyclerView.setAdapter(asksAdapter);
        asksRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Create the recycler view for the own asks grid
        RecyclerView ownAsksRecyclerView = (RecyclerView) findViewById(R.id.sellDenariiOwnAsksRecyclerView);

        OwnAskRecyclerViewAdapter ownAsksAdapter = new OwnAskRecyclerViewAdapter(getOwnAsks(), (askIds) -> {
            cancelAsks(askIds);
            return null;
        });
        ownAsksRecyclerView.setAdapter(ownAsksAdapter);
        ownAsksRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Create the recycler view for the pending sales grid
        RecyclerView pendingSalesRecyclerView = (RecyclerView) findViewById(R.id.sellDenariiPendingSalesRecyclerView);

        PendingSaleRecyclerViewAdapter pendingSalesAdapter = new PendingSaleRecyclerViewAdapter(getPendingSales());
        pendingSalesRecyclerView.setAdapter(pendingSalesAdapter);
        pendingSalesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    @Override
    public void onRefresh() {
        getNewAsks();
        refreshOwnAsks();
        refreshAsksInEscrow();
        refreshGoingPrice();
    }

    private List<Ask> getCurrentAsks() {
        List<Ask> asks = new ArrayList<>();

        for (DenariiAsk ask : currentAsks) {
            asks.add(new Ask(ask.getAskID(), ask.getAmount(), ask.getAskingPrice()));
        }

        return asks;
    }

    private List<OwnAsk> getOwnAsks() {
        List<OwnAsk> asks = new ArrayList<>();

        for (DenariiAsk ask : ownAsks) {
            asks.add(new OwnAsk(ask.getAskID(), ask.getAmount(), ask.getAskingPrice()));
        }

        return asks;
    }

    private List<PendingSale> getPendingSales() {
        List<PendingSale> pendingSales = new ArrayList<>();

        for (DenariiAsk ask : ownAsksBought) {
            pendingSales.add(new PendingSale(ask.getAskID(), ask.getAmount(), ask.getAskingPrice(), ask.getAmountBought()));
        }

        return pendingSales;
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
