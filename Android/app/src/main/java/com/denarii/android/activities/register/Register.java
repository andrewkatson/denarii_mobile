package com.denarii.android.activities.register;

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
import com.denarii.android.user.UserDetails;
import com.denarii.android.util.DenariiServiceHandler;
import com.denarii.android.util.UnpackDenariiResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    UserDetails userDetails = new UserDetails();

    EditText password = findViewById(R.id.register_enter_password_edit_text);

    EditText confirmPassword = findViewById(R.id.register_confirm_password_edit_text);

    Button submit = (Button) findViewById(R.id.register_submit_button);

    submit.setOnClickListener(
        v -> {
          EditText name = (EditText) findViewById(R.id.register_enter_name_edit_text);
          if (!isValidInput(name, Constants.ALPHANUMERIC_PATTERN)) {
            createFailureToast("Not a valid name");
            return;
          }
          EditText email = (EditText) findViewById(R.id.register_enter_email_edit_text);
          if (!isValidInput(email, Patterns.EMAIL_ADDRESS)) {
            createFailureToast("Not a valid email");
            return;
          }

          if (!confirmPassword.getText().toString().equals(password.getText().toString())) {
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

          userDetails.setUserName(name.getText().toString());
          userDetails.setUserEmail(email.getText().toString());
          userDetails.setUserPassword(password.getText().toString());

          getUserId(userDetails);
        });

    Button next = (Button) findViewById(R.id.register_next_button);

    next.setOnClickListener(
        v -> {
          Intent intent = new Intent(Register.this, Login.class);

          intent.putExtra(Constants.USER_DETAILS, userDetails);

          startActivity(intent);
        });
  }

  private void getUserId(UserDetails userDetails) {
    if (userDetails == null) {
      userDetails = UnpackDenariiResponse.validUserDetails();
    }
    DenariiService denariiService = DenariiServiceHandler.returnDenariiService();
    Call<List<DenariiResponse>> walletCall =
        denariiService.register(
            userDetails.getUserName(), userDetails.getUserEmail(), userDetails.getUserPassword());
    final UserDetails[] finalUserDetails = {userDetails};
    walletCall.enqueue(
        new Callback<List<DenariiResponse>>() {
          @Override
          public void onResponse(
              @NonNull Call<List<DenariiResponse>> call,
              @NonNull Response<List<DenariiResponse>> response) {
            if (response.isSuccessful()) {
              if (response.body() != null) {
                // We only care about the first wallet.
                boolean succeeded =
                    UnpackDenariiResponse.unpackLoginOrRegister(
                        finalUserDetails[0], response.body());
                if (succeeded) {
                  createSuccessToast();
                } else {
                  createFailureToast("Failed to register");
                }
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
    CharSequence text = "Registered Account";
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, text, duration);
    toast.show();

    Button next = (Button) findViewById(R.id.register_next_button);
    next.setVisibility(View.VISIBLE);
  }

  private void createFailureToast(String message) {
    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, message, duration);
    toast.show();
  }
}
