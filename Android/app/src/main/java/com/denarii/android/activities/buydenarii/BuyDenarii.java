package com.denarii.android.activities.buydenarii;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.denarii.android.R;
import com.denarii.android.activities.creditcardinfo.CreditCardInfo;
import com.denarii.android.activities.openedwallet.OpenedWallet;
import com.denarii.android.activities.selldenarii.SellDenarii;
import com.denarii.android.activities.usersettings.UserSettings;
import com.denarii.android.activities.verification.Verification;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.ui.models.Ask;
import com.denarii.android.ui.models.QueuedBuy;
import com.denarii.android.ui.recyclerviewadapters.BuyDenariiAskRecyclerViewAdapter;
import com.denarii.android.ui.recyclerviewadapters.QueuedBuyRecyclerViewAdapter;
import com.denarii.android.user.DenariiAsk;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.UserDetails;
import com.denarii.android.util.DenariiServiceHandler;
import com.denarii.android.util.UnpackDenariiResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyDenarii extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final List<DenariiAsk> currentAsks = new ArrayList<>();

    private BuyDenariiAskRecyclerViewAdapter asksRecyclerViewAdapter;

    private final List<Ask> allAsks = new ArrayList<>();

    private QueuedBuyRecyclerViewAdapter queuedBuyRecyclerViewAdapter;

    private final List<QueuedBuy> allQueuedBuys = new ArrayList<>();

    private List<DenariiAsk> queuedBuys = new ArrayList<>();

    private DenariiService denariiService;

    private UserDetails userDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_denarii);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.buy_denarii_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        denariiService = DenariiServiceHandler.returnDenariiService();

        Intent currentIntent = getIntent();
        userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

        getNewAsks();
        refreshSettledTransactions();

        getCurrentAsks();
        getQueuedBuys();

        Button submit = (Button) findViewById(R.id.buy_denarii_submit);

        submit.setOnClickListener(
                v -> {
                    onSubmitClicked();
                });

        // Create the recycler view for the asks grid
        RecyclerView asksRecyclerView = (RecyclerView) findViewById(R.id.buyDenariiAsksRecyclerView);

        asksRecyclerViewAdapter = new BuyDenariiAskRecyclerViewAdapter(allAsks);
        asksRecyclerView.setAdapter(asksRecyclerViewAdapter);
        asksRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Create the recycler view for the queued buys grid
        RecyclerView queuedBuysRecyclerView = (RecyclerView) findViewById(R.id.buyDenariiQueuedBuysRecyclerView);

        queuedBuyRecyclerViewAdapter = new QueuedBuyRecyclerViewAdapter(allQueuedBuys, (askIds) -> {
            cancelBuys(askIds);
            return null;
        });
        queuedBuysRecyclerView.setAdapter(queuedBuyRecyclerViewAdapter);
        queuedBuysRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override
    public void onRefresh() {
        getNewAsks();
        refreshSettledTransactions();

        updateAsksRecyclerView();
        updateQueuedBuysRecyclerView();
    }

    private void getCurrentAsks() {
        allAsks.clear();
        for (DenariiAsk ask : currentAsks) {
            allAsks.add(new Ask(ask.getAskID(), ask.getAmount(), ask.getAskingPrice()));
        }
    }

    private void getQueuedBuys() {
        allQueuedBuys.clear();
        for (DenariiAsk buy : queuedBuys) {
            allQueuedBuys.add(new QueuedBuy(buy.getAskID(), buy.getAmount(), buy.getAskingPrice(), buy.getAmountBought()));
        }
    }

    private void updateAsksRecyclerView() {
        int itemCountMinusOne = asksRecyclerViewAdapter.getItemCount() - 1;
        for (int i = itemCountMinusOne; i >= 0; i--) {
            asksRecyclerViewAdapter.notifyItemRemoved(i);
        }

        getCurrentAsks();

        int pos = 0;
        for (Ask unused : allAsks) {
            asksRecyclerViewAdapter.notifyItemInserted(pos);
            pos += 1;
        }
    }

    private void updateQueuedBuysRecyclerView() {
        int itemCountMinusOne = queuedBuyRecyclerViewAdapter.getItemCount() - 1;
        for (int i = itemCountMinusOne; i >= 0; i--) {
            queuedBuyRecyclerViewAdapter.notifyItemRemoved(i);
        }

        getQueuedBuys();

        int pos = 0;
        for (QueuedBuy unused : allQueuedBuys) {
            queuedBuyRecyclerViewAdapter.notifyItemInserted(pos);
            pos += 1;
        }
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

            final UserDetails[] finalDetails = {userDetails};

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
                                    UnpackDenariiResponse.unpackHasCreditCardInfo(finalDetails[0], response.body());
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
                                    UnpackDenariiResponse.unpackIsAVerifiedPerson(finalDetails[0], response.body());
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
                            // TODO log this
                        }
                    });

            if (finalDetails[0].getDenariiUser().getIsVerified()
                    && finalDetails[0].getCreditCard().getHasCreditCardInfo()) {

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
                                finalDetails[0].getUserID(),
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
                            denariiService.getMoneyFromBuyer(finalDetails[0].getUserID(), amount, "usd");

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
                                    denariiService.transferDenarii(finalDetails[0].getUserID(), ask.getAskID());

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
