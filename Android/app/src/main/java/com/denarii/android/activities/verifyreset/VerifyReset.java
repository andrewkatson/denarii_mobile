package com.denarii.android.activities.verifyreset;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.denarii.android.R;
import com.denarii.android.activities.resetpassword.ResetPassword;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.util.DenariiServiceHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyReset extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_reset);

        EditText resetId = findViewById(R.id.verify_reset_reset_id_edit_text);
        resetId.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button verifyResetButton = (Button) findViewById(R.id.verify_reset_verify_reset_button);

        verifyResetButton.setOnClickListener(v -> {
            verifyReset();
        });

        Button next = (Button) findViewById(R.id.verify_reset_next_button);

        next.setOnClickListener(v -> {
            Intent intent = new Intent(VerifyReset.this, ResetPassword.class);

            startActivity(intent);
        });
    }

    private void verifyReset() {

        Intent currentIntent = getIntent();
        String usernameOrEmail = (String) currentIntent.getSerializableExtra(Constants.RESET_PASSWORD_USERNAME_OR_EMAIL);

        EditText resetId = (EditText) findViewById(R.id.verify_reset_reset_id_edit_text);

        DenariiService denariiService = DenariiServiceHandler.returnDenariiService();

        try {
            Call<List<DenariiResponse>> walletCall = denariiService.verifyReset(usernameOrEmail, Integer.parseInt(resetId.getText().toString()));
            walletCall.enqueue(new Callback<List<DenariiResponse>>() {
                @Override
                public void onResponse(@NonNull Call<List<DenariiResponse>> call, @NonNull Response<List<DenariiResponse>> response) {
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
                public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
                    createFailureToast(String.format("%s %s", "Response failed", t));
                }
            });
        } catch (NumberFormatException e) {
            createFailureToast("Not a valid reset identifier");
        }

    }

    private void createSuccessToast() {
        Context context = getApplicationContext();
        CharSequence text = "Verified Reset";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Button next = (Button) findViewById(R.id.verify_reset_next_button);
        next.setVisibility(View.VISIBLE);
    }

    private void createFailureToast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}