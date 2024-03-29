package com.denarii.android.activities.openedwallet;

import static com.denarii.android.util.InputPatternValidator.isValidInput;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.denarii.android.R;
import com.denarii.android.activities.buydenarii.BuyDenarii;
import com.denarii.android.activities.creditcardinfo.CreditCardInfo;
import com.denarii.android.activities.selldenarii.SellDenarii;
import com.denarii.android.activities.usersettings.UserSettings;
import com.denarii.android.activities.verification.Verification;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.UserDetails;
import com.denarii.android.util.DenariiServiceHandler;
import com.denarii.android.util.UnpackDenariiResponse;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenedWallet extends AppCompatActivity {

  private UserDetails userDetails = null;

  private DenariiService denariiService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_opened_wallet);

    // Find the toolbar view inside the activity layout
    Toolbar toolbar = (Toolbar) findViewById(R.id.opened_wallet_toolbar);
    // Sets the Toolbar to act as the ActionBar for this Activity window.
    // Make sure the toolbar exists in the activity and is not null
    setSupportActionBar(toolbar);

    denariiService = DenariiServiceHandler.returnDenariiService();

    Intent currentIntent = getIntent();
    userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

    if (userDetails != null && userDetails.getWalletDetails() != null) {
      TextView seed = (TextView) findViewById(R.id.opened_wallet_seed_text_view);
      seed.setText(
          String.format(
              Locale.US,
              "%s %s",
              getString(R.string.opened_wallet_seed_text),
              userDetails.getWalletDetails().getSeed()));
    }

    getBalance(userDetails);

    Button send = (Button) findViewById(R.id.opened_wallet_attempt_send_button);

    send.setOnClickListener(v -> attemptToSendMoney(userDetails));

    Button refresh = (Button) findViewById(R.id.opened_wallet_refresh_balance_button);

    refresh.setOnClickListener(v -> getBalance(userDetails));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  private void getBalance(UserDetails localUserDetails) {
    if (localUserDetails == null) {
      localUserDetails = UnpackDenariiResponse.validUserDetails();
    }
    Call<List<DenariiResponse>> walletCall =
        denariiService.getBalance(
            localUserDetails.getUserID(), localUserDetails.getWalletDetails().getWalletName());

    final UserDetails[] finalUserDetails = {localUserDetails};
    walletCall.enqueue(
        new Callback<List<DenariiResponse>>() {
          @Override
          public void onResponse(
              @NonNull Call<List<DenariiResponse>> call,
              @NonNull Response<List<DenariiResponse>> response) {
            if (response.isSuccessful()) {
              if (response.body() != null) {
                // We only care about the first wallet.
                boolean success =
                    UnpackDenariiResponse.unpackGetBalance(finalUserDetails[0], response.body());
                if (success) {
                  createSuccessToast(
                      "Got Balance",
                      finalUserDetails[0].getWalletDetails().getBalance(),
                      finalUserDetails[0].getWalletDetails().getWalletAddress());
                } else {
                  createFailureToast("Failed to get balance");
                }
              } else {
                createFailureToast("Response body was null for get balance");
              }
            } else {
              createFailureToast("Response was not successful for get balance");
            }
          }

          @Override
          public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
            createFailureToast(String.format("%s %s", "Response failed for get balance", t));
          }
        });
  }

  private void attemptToSendMoney(UserDetails userDetails) {
    if (userDetails == null) {
      userDetails = UnpackDenariiResponse.validUserDetails();
    }

    EditText amount = (EditText) findViewById(R.id.opened_wallet_amount_edit_text);
    if (!isValidInput(amount, Constants.DOUBLE_PATTERN)) {
      createFailureToast("Not a valid amount");
      return;
    }
    EditText sendTo = (EditText) findViewById(R.id.opened_wallet_to_edit_text);
    if (!isValidInput(sendTo, Constants.ALPHANUMERIC_PATTERN)) {
      createFailureToast("Not a valid address");
      return;
    }
    TextView balance = findViewById(R.id.opened_wallet_balance_text_view);

    Pattern pattern = Pattern.compile("Balance: (\\d+\\.\\d+)");
    Matcher matcher = pattern.matcher(balance.getText().toString());
    String balanceText = "0.0";

    if (matcher.find()) {
      balanceText = matcher.group(1);
    }
    assert balanceText != null;
    if (Double.parseDouble(balanceText) < Double.parseDouble(amount.getText().toString())) {
      createFailureToast("Balance was less than the amount to send");
      return;
    }

    try {
      Call<List<DenariiResponse>> walletCall =
          denariiService.sendDenarii(
              userDetails.getUserID(),
              userDetails.getWalletDetails().getWalletName(),
              sendTo.getText().toString(),
              Double.parseDouble(amount.getText().toString()));

      final UserDetails[] finalUserDetails = {userDetails};
      walletCall.enqueue(
          new Callback<List<DenariiResponse>>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  // Update the balance now that we sent denarii.
                  getBalance(finalUserDetails[0]);

                  createSuccessToast(
                      "Sent Money",
                      finalUserDetails[0].getWalletDetails().getBalance(),
                      finalUserDetails[0].getWalletDetails().getWalletAddress());
                } else {
                  createFailureToast("Response body was null for send money");
                }
              } else {
                createFailureToast("Response was not successful for send money");
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
              createFailureToast(String.format("%s %s", "Response failed for send money", t));
            }
          });
    } catch (NumberFormatException e) {
      createFailureToast("Not a number provided for amount to send");
    }
  }

  private void createSuccessToast(String successMessage, double newBalance, String walletAddress) {
    TextView balance = (TextView) findViewById(R.id.opened_wallet_balance_text_view);

    balance.setText(
        String.format(
            Locale.US, "%s %f", getString(R.string.opened_wallet_balance_text), newBalance));

    TextView address = (TextView) findViewById(R.id.opened_wallet_address_text_view);

    address.setText(
        String.format(
            Locale.US, "%s %s", getString(R.string.opened_wallet_address_text), walletAddress));

    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, successMessage, duration);
    toast.show();
  }

  private void createFailureToast(String failureMessage) {
    TextView balance = (TextView) findViewById(R.id.opened_wallet_balance_text_view);

    balance.setText(
        String.format(Locale.US, "%s: %f", getString(R.string.opened_wallet_balance_text), 0.0d));

    TextView address = (TextView) findViewById(R.id.opened_wallet_address_text_view);

    address.setText(
        String.format(Locale.US, "%s: %s", getString(R.string.opened_wallet_address_text), ""));

    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, failureMessage, duration);
    toast.show();
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (Objects.equals(userDetails, null)) {
      return true;
    }

    // Handle item selection.
    if (Objects.equals(item.getItemId(), R.id.buy_denarii)) {
      Intent intent = new Intent(OpenedWallet.this, BuyDenarii.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.sell_denarii)) {
      Intent intent = new Intent(OpenedWallet.this, SellDenarii.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.wallet)) {
      return true;
    } else if (Objects.equals(item.getItemId(), R.id.verification)) {
      Intent intent = new Intent(OpenedWallet.this, Verification.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.credit_card_info)) {
      Intent intent = new Intent(OpenedWallet.this, CreditCardInfo.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.settings)) {
      Intent intent = new Intent(OpenedWallet.this, UserSettings.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }
}
