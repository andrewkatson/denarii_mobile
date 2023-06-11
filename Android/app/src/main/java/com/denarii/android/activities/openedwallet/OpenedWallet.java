package com.denarii.android.activities.openedwallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.denarii.android.R;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.user.UserDetails;
import com.denarii.android.user.Wallet;
import com.denarii.android.user.WalletDetails;
import com.denarii.android.util.DenariiServiceHandler;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenedWallet extends AppCompatActivity {

    private DenariiService denariiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_wallet);

        denariiService = DenariiServiceHandler.returnDenariiService();

        Intent currentIntent = getIntent();
        UserDetails userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

        TextView seed = (TextView) findViewById(R.id.opened_wallet_seed_text_view);
        seed.setText(String.format("Seed %s", userDetails.getWalletDetails().seed));

        getBalance(userDetails);

        Button send = (Button) findViewById(R.id.opened_wallet_attempt_send_button);

        send.setOnClickListener(v -> attemptToSendMoney(userDetails));

        Button refresh = (Button) findViewById(R.id.opened_wallet_refresh_balance_button);

        refresh.setOnClickListener(v -> getBalance(userDetails));
    }

    private void getBalance(UserDetails userDetails) {
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setWalletDetails(new WalletDetails());
        }
        Call<List<Wallet>> walletCall = denariiService.getBalance(userDetails.getWalletDetails().userIdentifier, userDetails.getWalletDetails().walletName);

        UserDetails finalUserDetails = userDetails;
        walletCall.enqueue(new Callback<List<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // We only care about the first wallet.
                        finalUserDetails.getWalletDetails().balance = response.body().get(0).response.balance;
                        createSuccessToast("Got Balance",
                                finalUserDetails.getWalletDetails().balance,
                                finalUserDetails.getWalletDetails().walletAddress);
                    } else {
                        createFailureToast("Response body was null for get balance");
                    }
                } else {
                    createFailureToast("Response was not successful for get balance");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                createFailureToast(String.format("%s %s", "Response failed for get balance", t));
            }
        });
    }

    private void attemptToSendMoney(UserDetails userDetails) {
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setWalletDetails(new WalletDetails());
        }

        EditText amount = (EditText) findViewById(R.id.opened_wallet_amount_edit_text);
        EditText sendTo = (EditText) findViewById(R.id.opened_wallet_to_edit_text);

        TextView balance = findViewById(R.id.opened_wallet_balance_text_view);

        if (Double.parseDouble(balance.getText().toString()) < Double.parseDouble(amount.getText().toString())) {
            createFailureToast("Balance was less than the amount to send");
            return;
        }

        try {
            Call<List<Wallet>> walletCall = denariiService.sendDenarii(userDetails.getWalletDetails().userIdentifier,
                    userDetails.getWalletDetails().walletName, sendTo.getText().toString(),
                    Double.parseDouble(amount.getText().toString()));

            UserDetails finalUserDetails = userDetails;
            walletCall.enqueue(new Callback<List<Wallet>>() {
                @Override
                public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            // Update the balance now that we sent denarii.
                            getBalance(finalUserDetails);

                            createSuccessToast("Sent Money",
                                    finalUserDetails.getWalletDetails().balance,
                                    finalUserDetails.getWalletDetails().walletAddress);
                        } else {
                            createFailureToast("Response body was null for send money");
                        }
                    } else {
                        createFailureToast("Response was not successful for send money");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                    createFailureToast(String.format("%s %s", "Response failed for send money", t));
                }
            });
        } catch (NumberFormatException e) {
            createFailureToast("Not a number provided for amount to send");
        }
    }

    private void createSuccessToast(String successMessage, double newBalance, String walletAddress) {
        TextView balance = (TextView) findViewById(R.id.opened_wallet_balance_text_view);

        balance.setText(String.format(Locale.US, "%s: %f", getString(R.string.opened_wallet_balance_text), newBalance));

        TextView address = (TextView) findViewById(R.id.opened_wallet_address_text_view);

        address.setText(String.format(Locale.US, "%s: %s", getString(R.string.opened_wallet_address_text), walletAddress));


        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, successMessage, duration);
        toast.show();
    }

    private void createFailureToast(String failureMessage) {
        TextView balance = (TextView) findViewById(R.id.opened_wallet_balance_text_view);

        balance.setText(String.format(Locale.US, "%s: %f", getString(R.string.opened_wallet_balance_text), 0.0d));

        TextView address = (TextView) findViewById(R.id.opened_wallet_address_text_view);

        address.setText(String.format(Locale.US, "%s: %s", getString(R.string.opened_wallet_address_text), ""));

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, failureMessage, duration);
        toast.show();
    }
}