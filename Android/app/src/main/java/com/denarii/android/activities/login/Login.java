package com.denarii.android.activities.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.denarii.android.R;
import com.denarii.android.activities.requestreset.RequestReset;
import com.denarii.android.activities.walletdecision.WalletDecision;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UserDetails userDetails = new UserDetails();

        EditText password = findViewById(R.id.login_enter_password_edit_text);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button submit = (Button)findViewById(R.id.login_submit_button);

        submit.setOnClickListener(v -> {

            EditText name = (EditText)findViewById(R.id.login_enter_name_edit_text);
            EditText email = (EditText)findViewById(R.id.login_enter_email_edit_text);

            userDetails.setUserName(name.getText().toString());
            userDetails.setUserEmail(email.getText().toString());
            userDetails.setUserPassword(password.getText().toString());

            getWalletDetails(userDetails);
        });

        Button next = (Button) findViewById(R.id.login_next_button);

        next.setOnClickListener( v -> {
            Intent intent = new Intent(Login.this, WalletDecision.class);

            intent.putExtra(Constants.USER_DETAILS, userDetails);

            startActivity(intent);
        });

        Button requestReset = (Button) findViewById(R.id.login_forgot_password_button);

        requestReset.setOnClickListener( v -> {
            Intent intent = new Intent(Login.this, RequestReset.class);

            intent.putExtra(Constants.USER_DETAILS, userDetails);

            startActivity(intent);
        });
    }

    private void getWalletDetails(UserDetails userDetails) {
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setWalletDetails(new WalletDetails());
        }
        DenariiService denariiService = DenariiServiceHandler.returnDenariiService();
        Call<List<Wallet>> walletCall = denariiService.getUserId(userDetails.getUserName(),
                userDetails.getUserEmail(), userDetails.getUserPassword());
        UserDetails finalUserDetails = userDetails;
        walletCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // We only care about the first wallet.
                        finalUserDetails.setWalletDetails(response.body().get(0).response);
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
        CharSequence text = "Logged In";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Button next = (Button) findViewById(R.id.login_next_button);
        next.setVisibility(View.VISIBLE);
    }

    private void createFailureToast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}