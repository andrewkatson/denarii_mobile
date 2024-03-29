package com.denarii.android.activities.resetpassword;

import static com.denarii.android.util.InputPatternValidator.isValidInput;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.denarii.android.R;
import com.denarii.android.activities.login.Login;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.util.DenariiServiceHandler;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reset_password);

    EditText password = findViewById(R.id.reset_password_enter_password_edit_text);

    EditText confirmPassword = findViewById(R.id.reset_password_confirm_password_edit_text);

    Button resetPasswordButton = (Button) findViewById(R.id.reset_password_reset_password_button);

    resetPasswordButton.setOnClickListener(
        v -> {
          resetPassword();
        });

    Button next = (Button) findViewById(R.id.reset_password_next_button);

    next.setOnClickListener(
        v -> {
          Intent intent = new Intent(ResetPassword.this, Login.class);

          startActivity(intent);
        });
  }

  private void resetPassword() {

    EditText username = (EditText) findViewById(R.id.rs_enter_name_edit_text);
    if (!isValidInput(username, Constants.ALPHANUMERIC_PATTERN)) {
      createFailureToast("Not a valid name");
      return;
    }
    EditText email = (EditText) findViewById(R.id.rs_enter_email_edit_text);
    if (!isValidInput(email, Patterns.EMAIL_ADDRESS)) {
      createFailureToast("Not a valid email");
      return;
    }

    EditText password = (EditText) findViewById(R.id.reset_password_enter_password_edit_text);
    EditText confirmPassword =
        (EditText) findViewById(R.id.reset_password_confirm_password_edit_text);

    if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
      createFailureToast("Passwords do not match");
      return;
    }

    if (!isValidInput(password, Constants.PASSWORD_PATTERN)) {
      createFailureToast("Not a valid password");
      return;
    }

    if (!isValidInput(confirmPassword, Constants.PASSWORD_PATTERN)) {
      createFailureToast("Not a valid confirm password");
      return;
    }

    DenariiService denariiService = DenariiServiceHandler.returnDenariiService();
    Call<List<DenariiResponse>> walletCall =
        denariiService.resetPassword(
            username.getText().toString(),
            email.getText().toString(),
            password.getText().toString());
    walletCall.enqueue(
        new Callback<List<DenariiResponse>>() {
          @Override
          public void onResponse(
              @NonNull Call<List<DenariiResponse>> call,
              @NonNull Response<List<DenariiResponse>> response) {
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
  }

  private void createSuccessToast() {
    Context context = getApplicationContext();
    CharSequence text = "Reset Password";
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, text, duration);
    toast.show();

    Button next = (Button) findViewById(R.id.reset_password_next_button);
    next.setVisibility(View.VISIBLE);
  }

  private void createFailureToast(String message) {
    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, message, duration);
    toast.show();
  }
}
