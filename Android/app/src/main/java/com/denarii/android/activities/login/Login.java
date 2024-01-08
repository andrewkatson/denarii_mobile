package com.denarii.android.activities.login;

import static com.denarii.android.util.PasswordPatternValidator.isValidPassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.denarii.android.R;
import com.denarii.android.activities.requestreset.RequestReset;
import com.denarii.android.activities.walletdecision.WalletDecision;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.UserDetails;
import com.denarii.android.util.DenariiServiceHandler;
import com.denarii.android.util.PatternTextWatcher;
import com.denarii.android.util.UnpackDenariiResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    UserDetails userDetails = new UserDetails();

    EditText password = findViewById(R.id.login_enter_password_edit_text);

    Button submit = (Button) findViewById(R.id.login_submit_button);

    submit.setOnClickListener(
        v -> {
          EditText name = (EditText) findViewById(R.id.login_enter_name_edit_text);
          // Match any alphanumeric or underscore
          name.addTextChangedListener(new PatternTextWatcher(name, Constants.ALPHANUMERIC_PATTERN));
          EditText email = (EditText) findViewById(R.id.login_enter_email_edit_text);
          // Match any alphanumeric or underscore
          email.addTextChangedListener(new PatternTextWatcher(email, Constants.EMAIL_PATTERN));

          isValidPassword(password.getText().toString());

          userDetails.setUserName(name.getText().toString());
          userDetails.setUserEmail(email.getText().toString());
          userDetails.setUserPassword(password.getText().toString());

          getUserID(userDetails);
        });

    Button next = (Button) findViewById(R.id.login_next_button);

    next.setOnClickListener(
        v -> {
          Intent intent = new Intent(Login.this, WalletDecision.class);

          intent.putExtra(Constants.USER_DETAILS, userDetails);

          startActivity(intent);
        });

    Button requestReset = (Button) findViewById(R.id.login_forgot_password_button);

    requestReset.setOnClickListener(
        v -> {
          Intent intent = new Intent(Login.this, RequestReset.class);

          intent.putExtra(Constants.USER_DETAILS, userDetails);

          startActivity(intent);
        });
  }

  private void getUserID(UserDetails userDetails) {
    if (userDetails == null) {
      userDetails = UnpackDenariiResponse.validUserDetails();
    }
    DenariiService denariiService = DenariiServiceHandler.returnDenariiService();
    Call<List<DenariiResponse>> walletCall =
        denariiService.getUserId(
            userDetails.getUserName(), userDetails.getUserEmail(), userDetails.getUserPassword());
    final UserDetails[] finalUserDetails = {userDetails};
    final boolean[] succeeded = {false};
    walletCall.enqueue(
        new Callback<>() {
          @Override
          public void onResponse(
              @NonNull Call<List<DenariiResponse>> call,
              @NonNull Response<List<DenariiResponse>> response) {
            if (response.isSuccessful()) {
              if (response.body() != null) {
                succeeded[0] =
                    UnpackDenariiResponse.unpackLoginOrRegister(
                        finalUserDetails[0], response.body());
                if (succeeded[0]) {
                  createSuccessToast();
                } else {
                  createFailureToast("Failed to login");
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
