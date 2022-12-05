package com.denarii.android.activities.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.denarii.android.R;
import com.denarii.android.activities.login.Login;
import com.denarii.android.activities.walletdecision.WalletDecision;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.user.UserDetails;
import com.denarii.android.user.Wallet;
import com.denarii.android.user.WalletDetails;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UserDetails userDetails = new UserDetails();


        Button submit = (Button)findViewById(R.id.register_submit_button);

        submit.setOnClickListener(v -> {

            EditText name = (EditText)findViewById(R.id.register_enter_name_edit_text);
            EditText email = (EditText)findViewById(R.id.register_enter_email_edit_text);
            EditText password = (EditText) findViewById(R.id.register_enter_password_edit_text);
            EditText confirmPassword = (EditText) findViewById(R.id.register_confirm_password_edit_text);

            if (!confirmPassword.getText().toString().equals(password.getText().toString())) {
                createFailureTextView("Passwords do not match");
                return;
            }

            userDetails.setUserName(name.getText().toString());
            userDetails.setUserEmail(email.getText().toString());
            userDetails.setUserPassword(password.getText().toString());

            getWalletDetails(userDetails);
        });

        Button next = (Button) findViewById(R.id.register_next_button);

        next.setOnClickListener( v -> {
            Intent intent = new Intent(Register.this, WalletDecision.class);

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
        Call<List<Wallet>> walletCall = denariiService.getUserId(userDetails.getUserName(),
                userDetails.getUserEmail(), userDetails.getUserPassword());
        UserDetails finalUserDetails = userDetails;
        walletCall.enqueue(new Callback<List<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // We only care about the first wallet.
                        finalUserDetails.setWalletDetails(response.body().get(0).response);
                        createSuccessTextView();
                    } else {
                        createFailureTextView("No response body");
                    }
                } else {
                    createFailureTextView("Response was not successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                createFailureTextView(String.format("%s %s", "Response failed", t));
            }
        });
    }

    private void createSuccessTextView() {
        TextView successOrFailure = (TextView)findViewById(R.id.register_success_text_view);

        successOrFailure.setText(getString(R.string.register_success_text));
        successOrFailure.setVisibility(View.VISIBLE);

        Button next = (Button)findViewById(R.id.register_next_button);
        next.setVisibility(View.VISIBLE);
    }

    private void createFailureTextView(String failureMessage) {
        TextView successOrFailure = (TextView)findViewById(R.id.register_success_text_view);

        String textToShow = String.format(Locale.US, "%s: %s", getString(R.string.register_failure_text), failureMessage);
        successOrFailure.setText(textToShow);

        successOrFailure.setVisibility(View.VISIBLE);
    }
}