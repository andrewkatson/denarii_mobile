package com.denarii.android.activities.requestreset;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.denarii.android.R;
import com.denarii.android.activities.openedwallet.OpenedWallet;
import com.denarii.android.activities.openwallet.OpenWallet;
import com.denarii.android.activities.verifyreset.VerifyReset;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.user.UserDetails;
import com.denarii.android.user.Wallet;
import com.denarii.android.user.WalletDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestReset extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_reset);

        Button requestReset = (Button) findViewById(R.id.request_reset_request_reset_button);

        requestReset.setOnClickListener(v -> {
            requestTheReset();
        });

        Button next = (Button) findViewById(R.id.request_reset_next_button);

        next.setOnClickListener(v -> {
            Intent intent = new Intent(RequestReset.this, VerifyReset.class);

            EditText usernameOrEmail = (EditText) findViewById(R.id.request_reset_username_or_email_edit_text);

            intent.putExtra(Constants.RESET_PASSWORD_USERNAME_OR_EMAIL, usernameOrEmail.getText().toString());

            startActivity(intent);
        });
    }

    private void requestTheReset() {

        EditText usernameOrEmail = (EditText) findViewById(R.id.request_reset_username_or_email_edit_text);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        DenariiService denariiService = retrofit.create(DenariiService.class);
        Call<List<Wallet>> walletCall = denariiService.requestPasswordReset(usernameOrEmail.getText().toString());
        walletCall.enqueue(new Callback<List<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        createSuccessToast();
                    } else {
                        createFailureToast("No response body");
                    }
                } else {
                    createFailureToast("Response was not successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                createFailureToast(String.format("%s %s", "Response failed", t));
            }
        });
    }

    private void createSuccessToast() {
        Context context = getApplicationContext();
        CharSequence text = "Requested Reset";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Button next = (Button) findViewById(R.id.request_reset_next_button);
        next.setVisibility(View.VISIBLE);
    }

    private void createFailureToast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}