package com.denarii.android.activities.creditcardinfo;

import static com.denarii.android.util.InputPatternValidator.isValidInput;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.denarii.android.R;
import com.denarii.android.activities.buydenarii.BuyDenarii;
import com.denarii.android.activities.openedwallet.OpenedWallet;
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
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreditCardInfo extends AppCompatActivity {
  private final ReentrantLock reentrantLock = new ReentrantLock();

  private DenariiService denariiService;

  private UserDetails userDetails = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_credit_card_info);

    // Find the toolbar view inside the activity layout
    Toolbar toolbar = (Toolbar) findViewById(R.id.credit_card_info_toolbar);
    // Sets the Toolbar to act as the ActionBar for this Activity window.
    // Make sure the toolbar exists in the activity and is not null
    setSupportActionBar(toolbar);

    denariiService = DenariiServiceHandler.returnDenariiService();

    Intent currentIntent = getIntent();
    userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

    checkCreditCardInfo();

    Button setCreditCardInfoButton = findViewById(R.id.creditCardInfoSubmit);

    setCreditCardInfoButton.setOnClickListener(
        v -> {
          setCreditCardInfo();
        });

    Button clearCreditCardInfoButton = findViewById(R.id.creditCardInfoClear);

    clearCreditCardInfoButton.setOnClickListener(
        v -> {
          clearCreditCardInfo();
        });
  }

  private void checkCreditCardInfo() {
    try {
      reentrantLock.lock();

      if (Objects.equals(userDetails, null)) {
        userDetails = UnpackDenariiResponse.validUserDetails();
      }

      final UserDetails[] finalDetails = {userDetails};

      Call<List<DenariiResponse>> call = denariiService.hasCreditCardInfo(userDetails.getUserID());
      call.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  UnpackDenariiResponse.unpackHasCreditCardInfo(finalDetails[0], response.body());
                  if (finalDetails[0].getCreditCard().getHasCreditCardInfo()) {
                    Button clearInfoButton = findViewById(R.id.creditCardInfoClear);
                    clearInfoButton.setVisibility(View.VISIBLE);
                  }
                  formatStatus();
                }
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
              // TODO log this
            }
          });
    } finally {
      reentrantLock.unlock();
    }
  }

  private void setCreditCardInfo() {
    try {
      reentrantLock.lock();

      if (Objects.equals(userDetails, null)) {
        userDetails = UnpackDenariiResponse.validUserDetails();
      }

      final UserDetails[] finalDetails = {userDetails};

      EditText numberEditText = findViewById(R.id.creditCardInfoNumber);
      if (!isValidInput(numberEditText, Constants.DIGITS_AND_DASHES_PATTERN)) {
        createToast("Not a valid credit card number");
        return;
      }
      String number = numberEditText.getText().toString();

      EditText expirationDateEditText = findViewById(R.id.creditCardInfoExpiration);
      String expirationDate = expirationDateEditText.getText().toString();
      if (!isValidInput(expirationDateEditText, Constants.SLASH_DATE_PATTERN)) {
        createToast("Not a valid expiration date");
        return;
      }
      String expirationDateMonth = "";
      String expirationDateYear = "";
      if (!expirationDate.isEmpty()) {
        String[] arr = expirationDate.split("/");
        expirationDateMonth = arr[0];
        expirationDateYear = arr[1];
      }

      EditText securityCodeEditText = findViewById(R.id.creditCardInfoSecurityCode);
      if (!isValidInput(securityCodeEditText, Constants.DIGITS_ONLY)) {
        createToast("Not a valid security code");
        return;
      }
      String securityCode = securityCodeEditText.getText().toString();

      Call<List<DenariiResponse>> call =
          denariiService.setCreditCardInfo(
              userDetails.getUserID(),
              number,
              expirationDateMonth,
              expirationDateYear,
              securityCode);
      final boolean[] succeeded = {false};
      call.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  succeeded[0] =
                      UnpackDenariiResponse.unpackSetCreditCardInfo(
                          finalDetails[0], response.body());
                  checkCreditCardInfo();

                  if (succeeded[0]) {
                    createToast("Set credit card info");

                    Button clearInfoButton = findViewById(R.id.creditCardInfoClear);
                    clearInfoButton.setVisibility(View.VISIBLE);
                  } else {
                    createToast("Failed to set credit card info");
                  }

                  formatStatus();
                }
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
              // TODO log this
            }
          });
    } finally {
      reentrantLock.unlock();
    }
  }

  private void clearCreditCardInfo() {
    try {
      reentrantLock.lock();

      if (Objects.equals(userDetails, null)) {
        userDetails = UnpackDenariiResponse.validUserDetails();
      }

      final UserDetails[] finalDetails = {userDetails};

      Call<List<DenariiResponse>> call =
          denariiService.clearCreditCardInfo(userDetails.getUserID());
      final boolean[] succeeded = {false};
      call.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  succeeded[0] =
                      UnpackDenariiResponse.unpackClearCreditCardInfo(
                          finalDetails[0], response.body());

                  if (succeeded[0]) {
                    createToast("Cleared credit card info");

                    Button clearInfoButton = findViewById(R.id.creditCardInfoClear);
                    clearInfoButton.setVisibility(View.INVISIBLE);
                  } else {
                    createToast("Failed to clear credit card info");
                  }

                  checkCreditCardInfo();
                }
              }
            }

            @Override
            public void onFailure(@NonNull Call<List<DenariiResponse>> call, @NonNull Throwable t) {
              // TODO log this
            }
          });
    } finally {
      reentrantLock.unlock();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (Objects.equals(userDetails, null)) {
      return true;
    }

    // Handle item selection.
    if (Objects.equals(item.getItemId(), R.id.buy_denarii)) {
      Intent intent = new Intent(CreditCardInfo.this, BuyDenarii.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.sell_denarii)) {
      Intent intent = new Intent(CreditCardInfo.this, SellDenarii.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.wallet)) {
      Intent intent = new Intent(CreditCardInfo.this, OpenedWallet.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.verification)) {
      Intent intent = new Intent(CreditCardInfo.this, Verification.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.credit_card_info)) {
      return true;
    } else if (Objects.equals(item.getItemId(), R.id.settings)) {
      Intent intent = new Intent(CreditCardInfo.this, UserSettings.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  private void formatStatus() {
    TextView statusTextView = findViewById(R.id.creditCardInfoStatus);
    if (Objects.equals(userDetails.getCreditCard(), null)) {
      statusTextView.setText(R.string.credit_card_info_does_not_have_info_status);
      return;
    }
    if (userDetails.getCreditCard().getHasCreditCardInfo()) {
      statusTextView.setText(R.string.credit_card_info_has_info_status);
    } else {
      statusTextView.setText(R.string.credit_card_info_does_not_have_info_status);
    }
  }

  private void createToast(String message) {
    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, message, duration);
    toast.show();
  }
}
