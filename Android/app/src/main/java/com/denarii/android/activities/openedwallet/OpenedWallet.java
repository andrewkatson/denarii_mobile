package com.denarii.android.activities.openedwallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.denarii.android.R;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.user.UserDetails;
import com.denarii.android.user.Wallet;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OpenedWallet extends AppCompatActivity {

    private DenariiService denariiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_wallet);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).build();
        denariiService = retrofit.create(DenariiService.class);

        Intent currentIntent = getIntent();
        UserDetails userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

        getBalance(userDetails);

        Button send = (Button) findViewById(R.id.opened_wallet_attempt_send_button);

        send.setOnClickListener(v -> attemptToSendMoney(userDetails));

        Button refresh = (Button) findViewById(R.id.opened_wallet_refresh_balance_button);

        refresh.setOnClickListener(v -> getBalance(userDetails));
    }

    private void getBalance(UserDetails userDetails) {
        Call<Wallet> walletCall = denariiService.getBalance(userDetails.getWalletDetails().userId, userDetails.getWalletDetails().walletName);

        walletCall.enqueue(new Callback<Wallet>() {
            @Override
            public void onResponse(@NonNull Call<Wallet> call, @NonNull Response<Wallet> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        userDetails.getWalletDetails().balance = response.body().response.balance;
                        createSuccessTextView("Got Balance", userDetails.getWalletDetails().balance);
                    } else {
                        createFailureTextView("Response body was null for get balance");
                    }
                } else {
                    createFailureTextView("Response was not successful for get balance");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Wallet> call, @NonNull Throwable t) {
                createFailureTextView("Response failed for get balance");
            }
        });
    }

    private void attemptToSendMoney(UserDetails userDetails) {

        EditText amount = (EditText) findViewById(R.id.opened_wallet_amount_edit_text);
        EditText sendTo = (EditText) findViewById(R.id.opened_wallet_to_edit_text);

        try {
            Call<Wallet> walletCall = denariiService.sendDenarii(userDetails.getWalletDetails().userId,
                    userDetails.getWalletDetails().walletName, sendTo.getText().toString(),
                    Double.parseDouble(amount.getText().toString()));

            walletCall.enqueue(new Callback<Wallet>() {
                @Override
                public void onResponse(@NonNull Call<Wallet> call, @NonNull Response<Wallet> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            // Update the balance now that we sent denarii.
                            getBalance(userDetails);

                            createSuccessTextView("Sent Money", userDetails.getWalletDetails().balance);
                        } else {
                            createFailureTextView("Response body was null for send money");
                        }
                    } else {
                        createFailureTextView("Response was not successful for send money");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Wallet> call, @NonNull Throwable t) {
                    createFailureTextView("Response failed for send money");
                }
            });
        } catch (NumberFormatException e) {
            createFailureTextView("Not a number provided for amount to send");
        }
    }

    private void createSuccessTextView(String successMessage, double newBalance) {
        TextView balance = (TextView) findViewById(R.id.opened_wallet_balance_text_view);

        balance.setText(String.format(Locale.US, "%s: %f", getString(R.string.opened_wallet_balance_text), newBalance));

        TextView success = (TextView) findViewById(R.id.opened_wallet_success_text_view);

        success.setText(String.format(Locale.US, "%s: %s", getString(R.string.opened_wallet_success_text), successMessage));
        success.setVisibility(View.VISIBLE);
    }

    private void createFailureTextView(String failureMessage) {
        TextView balance = (TextView) findViewById(R.id.opened_wallet_balance_text_view);

        balance.setText(String.format(Locale.US, "%s: %f", getString(R.string.opened_wallet_balance_text), 0.0d));

        TextView success = (TextView) findViewById(R.id.opened_wallet_success_text_view);

        success.setText(String.format(Locale.US, "%s: %s", getString(R.string.opened_wallet_failure_text), failureMessage));
        success.setVisibility(View.VISIBLE);
    }
}