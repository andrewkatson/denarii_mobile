package com.denarii.android.activities.createwallet;

import static com.denarii.android.util.InputPatternValidator.isValidInput;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.denarii.android.R;
import com.denarii.android.activities.openedwallet.OpenedWallet;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.UserDetails;
import com.denarii.android.util.DenariiServiceHandler;
import com.denarii.android.util.UnpackDenariiResponse;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateWallet extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_wallet);

    EditText password = findViewById(R.id.create_wallet_enter_password);

    EditText confirmPassword = findViewById(R.id.create_wallet_confirm_password);

    Button submit = (Button) findViewById(R.id.create_wallet_submit_button);

    submit.setOnClickListener(
        v -> {
          Intent currentIntent = getIntent();
          UserDetails userDetails =
              (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

          // Makes the call to create the wallet and then we check if the success box
          // has the right text to continue to the next activity.
          createWallet(userDetails);
        });

    Button next = (Button) findViewById(R.id.create_wallet_next_button);

    next.setOnClickListener(
        v -> {
          Intent currentIntent = getIntent();
          UserDetails userDetails =
              (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

          Intent intent = new Intent(CreateWallet.this, OpenedWallet.class);

          intent.putExtra(Constants.USER_DETAILS, userDetails);

          startActivity(intent);
        });
  }

  private void createWallet(UserDetails userDetails) {
    if (userDetails == null) {
      userDetails = UnpackDenariiResponse.validUserDetails();
    }
    EditText walletName = (EditText) findViewById(R.id.create_wallet_enter_name);
    if (!isValidInput(walletName, Constants.ALPHANUMERIC_PATTERN)) {
      createFailureToast("Not a valid wallet name");
      return;
    }
    EditText walletPassword = (EditText) findViewById(R.id.create_wallet_enter_password);
    EditText confirmPassword = findViewById(R.id.create_wallet_confirm_password);

    String walletPasswordText = walletPassword.getText().toString();
    String confirmWalletPasswordText = confirmPassword.getText().toString();

    if (!Objects.equals(walletPasswordText, confirmWalletPasswordText)) {
      createFailureToast("Passwords do not match");
      return;
    }

    if (!isValidInput(walletPassword, Constants.PASSWORD_PATTERN)) {
      createFailureToast("Not a valid password");
      return;
    }

    if (!isValidInput(confirmPassword, Constants.PASSWORD_PATTERN)) {
      createFailureToast("Not a valid confirm password");
      return;
    }

    userDetails.getWalletDetails().setWalletName(walletName.getText().toString());
    userDetails.getWalletDetails().setWalletPassword(walletPassword.getText().toString());

    DenariiService denariiService = DenariiServiceHandler.returnDenariiService();
    Call<List<DenariiResponse>> walletCall =
        denariiService.createWallet(
            userDetails.getUserID(), walletName.getText().toString(), walletPasswordText);

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
                    UnpackDenariiResponse.unpackCreateWallet(finalUserDetails[0], response.body());
                if (succeeded) {
                  createSuccessToast(finalUserDetails[0].getWalletDetails().getSeed());
                } else {
                  createFailureToast("Failed to create wallet");
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

  private void createSuccessToast(String seed) {
    Context context = getApplicationContext();
    CharSequence text = "Created Wallet";
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, text, duration);
    toast.show();

    Button next = (Button) findViewById(R.id.create_wallet_next_button);
    next.setVisibility(View.VISIBLE);

    TextView seedTextView = (TextView) findViewById(R.id.create_wallet_seed_text_view);
    seedTextView.setText(seed);
    seedTextView.setVisibility(View.VISIBLE);
  }

  private void createFailureToast(String message) {
    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, message, duration);
    toast.show();
  }
}
