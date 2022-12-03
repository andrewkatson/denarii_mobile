package com.denarii.android.activities.userinfo;

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
import com.denarii.android.activities.walletdecision.WalletDecision;
import com.denarii.android.user.Wallet;
import com.denarii.android.user.WalletDetails;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        UserDetails userDetails = new UserDetails();


        Button submit = (Button)findViewById(R.id.user_info_submit);

        submit.setOnClickListener(v -> {

            EditText name = (EditText)findViewById(R.id.user_info_enter_name);
            EditText email = (EditText)findViewById(R.id.user_info_enter_email);

            userDetails.setUserName(name.getText().toString());
            userDetails.setUserEmail(email.getText().toString());

            getWalletDetails(userDetails);
        });

        Button next = (Button) findViewById(R.id.user_info_next);

        next.setOnClickListener( v -> {
            Intent intent = new Intent(UserInfo.this, WalletDecision.class);

            intent.putExtra(Constants.USER_DETAILS, userDetails);

            startActivity(intent);
        });
    }

    private void getWalletDetails(UserDetails userDetails) {
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setWalletDetails(new WalletDetails());
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        DenariiService denariiService = retrofit.create(DenariiService.class);
        Call<Wallet> walletCall = denariiService.getUserId(userDetails.getUserName(), userDetails.getUserEmail());
        UserDetails finalUserDetails = userDetails;
        walletCall.enqueue(new Callback<Wallet>() {
            @Override
            public void onResponse(@NonNull Call<Wallet> call, @NonNull Response<Wallet> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        finalUserDetails.setWalletDetails(response.body().response);
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
        TextView successOrFailure = (TextView)findViewById(R.id.user_info_success_text_view);

        successOrFailure.setText(R.string.user_info_success_text);
        successOrFailure.setVisibility(View.VISIBLE);

        Button next = (Button)findViewById(R.id.user_info_next);
        next.setVisibility(View.VISIBLE);
    }

    private void createFailureTextView(String failureMessage) {
        TextView successOrFailure = (TextView)findViewById(R.id.user_info_success_text_view);

        String textToShow = String.format(Locale.US, "%s: %s", getString(R.string.user_info_failure_text), failureMessage);
        successOrFailure.setText(textToShow);

        successOrFailure.setVisibility(View.VISIBLE);
    }
}