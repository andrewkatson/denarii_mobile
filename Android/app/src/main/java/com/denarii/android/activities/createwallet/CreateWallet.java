package com.denarii.android.activities.createwallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.denarii.android.R;
import com.denarii.android.activities.openedwallet.OpenedWallet;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.user.UserDetails;
import com.denarii.android.user.Wallet;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateWallet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        Button submit = (Button) findViewById(R.id.create_wallet_submit_button);

        submit.setOnClickListener(v -> {
            Intent currentIntent = getIntent();
            UserDetails userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

            // Makes the call to create the wallet and then we check if the success box
            // has the right text to continue to the next activity.
            setupRetroFit(userDetails);

            if (success()) {
                Intent intent = new Intent(CreateWallet.this, OpenedWallet.class);

                intent.putExtra(Constants.USER_DETAILS, userDetails);

                startActivity(intent);
            }
        });
    }

    private void setupRetroFit(UserDetails userDetails) {
        EditText walletName = (EditText) findViewById(R.id.create_wallet_enter_name);
        EditText walletPassword = (EditText) findViewById(R.id.create_wallet_enter_password);

        userDetails.getWalletDetails().walletName = walletName.getText().toString();
        userDetails.getWalletDetails().walletPassword = walletPassword.getText().toString();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).build();
        DenariiService denariiService = retrofit.create(DenariiService.class);
        Call<Wallet> walletCall = denariiService.createWallet(userDetails.getWalletDetails().userIdentifier, walletName.getText().toString(), walletPassword.getText().toString());

        walletCall.enqueue(new Callback<Wallet>() {
            @Override
            public void onResponse(@NonNull Call<Wallet> call, @NonNull Response<Wallet> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        userDetails.getWalletDetails().seed = response.body().response.seed;
                        userDetails.getWalletDetails().walletAddress = response.body().response.walletAddress;
                        createSuccessTextView(userDetails.getWalletDetails().seed);
                    } else {
                        createFailureTextView("No response body");
                    }
                } else {
                    createFailureTextView("Response was not successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Wallet> call, @NonNull Throwable t) {
                createFailureTextView("Response failed");
            }
        });
    }

    private void createSuccessTextView(String userSeed) {
        TextView successOrFailure = (TextView) findViewById(R.id.create_wallet_success_text_view);

        successOrFailure.setText(R.string.create_wallet_success_text);
        successOrFailure.setVisibility(View.VISIBLE);

        TextView seed = (TextView) findViewById(R.id.create_wallet_seed_text_view);

        seed.setText(String.format(Locale.US, "Seed: %s", userSeed));
        seed.setVisibility(View.VISIBLE);
    }

    private void createFailureTextView(String failureMessage) {
        TextView successOrFailure = (TextView) findViewById(R.id.create_wallet_success_text_view);

        String textToShow = String.format(Locale.US, "%s: %s", getString(R.string.create_wallet_failure_text), failureMessage);
        successOrFailure.setText(textToShow);
    }

    private boolean success() {
        TextView successOrFailure = (TextView) findViewById(R.id.create_wallet_success_text_view);
        return successOrFailure.getText().toString().contains(getString(R.string.create_wallet_success_text));
    }
}