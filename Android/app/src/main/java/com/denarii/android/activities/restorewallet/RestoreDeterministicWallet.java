package com.denarii.android.activities.restorewallet;

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
import com.denarii.android.user.WalletDetails;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestoreDeterministicWallet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_deterministic_wallet);

        Button submit = (Button) findViewById(R.id.restore_wallet_submit_button);

        submit.setOnClickListener(v -> {
            Intent currentIntent = getIntent();
            UserDetails userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

            // Makes the call to create the wallet and then we check if the success box
            // has the right text to continue to the next activity.
            setupRetroFit(userDetails);

            if (success()) {
                Intent intent = new Intent(RestoreDeterministicWallet.this, OpenedWallet.class);

                intent.putExtra(Constants.USER_DETAILS, userDetails);

                startActivity(intent);
            }
        });
    }

    private void setupRetroFit(UserDetails userDetails) {
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setWalletDetails(new WalletDetails());
        }
        EditText walletName = (EditText) findViewById(R.id.restore_wallet_enter_name);
        EditText walletPassword = (EditText) findViewById(R.id.restore_wallet_enter_password);
        EditText walletSeed = (EditText) findViewById(R.id.restore_wallet_enter_seed);

        userDetails.getWalletDetails().walletName = walletName.getText().toString();
        userDetails.getWalletDetails().walletPassword = walletPassword.getText().toString();
        userDetails.getWalletDetails().seed = walletSeed.getText().toString();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        DenariiService denariiService = retrofit.create(DenariiService.class);
        Call<Wallet> walletCall = denariiService.restoreWallet(userDetails.getWalletDetails().userIdentifier, walletName.getText().toString(), walletPassword.getText().toString(), walletSeed.getText().toString());

        UserDetails finalUserDetails = userDetails;
        walletCall.enqueue(new Callback<Wallet>() {
            @Override
            public void onResponse(@NonNull Call<Wallet> call, @NonNull Response<Wallet> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        finalUserDetails.getWalletDetails().walletAddress = response.body().response.walletAddress;
                        createSuccessTextView();
                    } else {
                        createFailureTextView("No response body");
                    }
                } else {
                    createFailureTextView("Response was not successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Wallet> call, @NonNull Throwable t) {
                createFailureTextView(String.format("%s %s", "Response failed", t));
            }
        });
    }

    private void createSuccessTextView() {
        TextView successOrFailure = (TextView)findViewById(R.id.restore_wallet_success);

        successOrFailure.setText(R.string.restore_wallet_success_text);
        successOrFailure.setVisibility(View.VISIBLE);
    }

    private void createFailureTextView(String failureMessage) {
        TextView successOrFailure = (TextView)findViewById(R.id.restore_wallet_success);

        String textToShow = String.format(Locale.US, "%s: %s", getString(R.string.restore_wallet_failure_text), failureMessage);
        successOrFailure.setText(textToShow);

        successOrFailure.setVisibility(View.VISIBLE);
    }

    private boolean success() {
        TextView successOrFailure = (TextView) findViewById(R.id.restore_wallet_success);
        return successOrFailure.getText().toString().contains(getString(R.string.restore_wallet_success_text));
    }
}