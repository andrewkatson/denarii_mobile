package com.denarii.android.activities.verification;

import static com.denarii.android.util.InputPatternValidator.isValidInput;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.denarii.android.R;
import com.denarii.android.activities.buydenarii.BuyDenarii;
import com.denarii.android.activities.creditcardinfo.CreditCardInfo;
import com.denarii.android.activities.openedwallet.OpenedWallet;
import com.denarii.android.activities.selldenarii.SellDenarii;
import com.denarii.android.activities.usersettings.UserSettings;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.DenariiUser;
import com.denarii.android.user.UserDetails;
import com.denarii.android.util.DenariiServiceHandler;
import com.denarii.android.util.UnpackDenariiResponse;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Verification extends AppCompatActivity
    implements SwipeRefreshLayout.OnRefreshListener {

  private final ReentrantLock reentrantLock = new ReentrantLock();

  private DenariiService denariiService;

  private UserDetails userDetails = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_verification);

    // Find the toolbar view inside the activity layout
    Toolbar toolbar = (Toolbar) findViewById(R.id.verification_toolbar);
    // Sets the Toolbar to act as the ActionBar for this Activity window.
    // Make sure the toolbar exists in the activity and is not null
    setSupportActionBar(toolbar);

    denariiService = DenariiServiceHandler.returnDenariiService();

    Intent currentIntent = getIntent();
    userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

    lookupVerificationStatus();

    Button submitVerification = findViewById(R.id.verificationSubmit);

    submitVerification.setOnClickListener(
        v -> {
          submitVerification();
        });
  }

  @Override
  public void onRefresh() {
    lookupVerificationStatus();
  }

  private void lookupVerificationStatus() {
    try {
      reentrantLock.lock();

      if (Objects.equals(userDetails, null)) {
        userDetails = UnpackDenariiResponse.validUserDetails();
      }

      final UserDetails[] finalUserDetails = {userDetails};

      Call<List<DenariiResponse>> call = denariiService.isAVerifiedPerson(userDetails.getUserID());
      call.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  UnpackDenariiResponse.unpackIsAVerifiedPerson(
                      finalUserDetails[0], response.body());

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

  private void submitVerification() {
    try {
      reentrantLock.lock();

      if (Objects.equals(userDetails, null)) {
        userDetails = UnpackDenariiResponse.validUserDetails();
      }

      final UserDetails[] finalUserDetails = {userDetails};

      EditText firstNameTextView = findViewById(R.id.firstName);
      if (!isValidInput(firstNameTextView, Constants.NAME_PATTERN)) {
        createToast("Not a valid first name");
        return;
      }
      String firstName = firstNameTextView.getText().toString();

      EditText middleInitialTextView = findViewById(R.id.middleInitial);
      if (!isValidInput(middleInitialTextView, Constants.SINGLE_LETTER_PATTERN)) {
        createToast("Not a valid middle initial");
        return;
      }

      String middleInital = middleInitialTextView.getText().toString();

      EditText lastNameTextView = findViewById(R.id.lastName);
      if (!isValidInput(lastNameTextView, Constants.NAME_PATTERN)) {
        createToast("Not a valid last name");
        return;
      }
      String lastName = lastNameTextView.getText().toString();

      EditText emailTextView = findViewById(R.id.email);
      if (!isValidInput(emailTextView, Patterns.EMAIL_ADDRESS)) {
        createToast("Not a valid email");
        return;
      }
      String email = emailTextView.getText().toString();

      EditText dobTextView = findViewById(R.id.dateOfBirth);
      if (!isValidInput(dobTextView, Constants.SLASH_DATE_PATTERN)) {
        createToast("Not a valid date of birth");
        return;
      }
      String dob = dobTextView.getText().toString();

      EditText ssnTextView = findViewById(R.id.socialSecurityNumber);
      if (!isValidInput(ssnTextView, Constants.DIGITS_AND_DASHES_PATTERN)) {
        createToast("Not a valid social security number");
        return;
      }
      String ssn = ssnTextView.getText().toString();

      EditText zipCodeTextView = findViewById(R.id.zipCode);
      if (!isValidInput(zipCodeTextView, Constants.DIGITS_AND_DASHES_PATTERN)) {
        createToast("Not a valid zip code");
        return;
      }
      String zipCode = zipCodeTextView.getText().toString();

      EditText phoneNumberTextView = findViewById(R.id.verificationPhoneNumber);
      if (!isValidInput(phoneNumberTextView, Constants.PHONE_NUMBER_PATTERN)) {
        createToast("Not a valid phone number");
        return;
      }
      String phoneNumber = phoneNumberTextView.getText().toString();

      EditText workCityTextView = findViewById(R.id.workCity);
      if (!isValidInput(workCityTextView, Constants.ALPHANUMERIC_PATTERN_WITH_SPACES)) {
        createToast("Not a valid work city");
        return;
      }
      String workCity = workCityTextView.getText().toString();

      EditText workCountryTextView = findViewById(R.id.workCountry);
      if (!isValidInput(workCountryTextView, Constants.ALPHANUMERIC_PATTERN_WITH_SPACES)) {
        createToast("Not a valid work country");
        return;
      }
      String workCountry = workCountryTextView.getText().toString();

      EditText workStateTextView = findViewById(R.id.workState);
      if (!isValidInput(workStateTextView, Constants.ALPHANUMERIC_PATTERN_WITH_SPACES)) {
        createToast("Not a valid work state");
        return;
      }
      String workState = workStateTextView.getText().toString();

      Call<List<DenariiResponse>> call =
          denariiService.verifyIdentity(
              userDetails.getUserID(),
              firstName,
              middleInital,
              lastName,
              email,
              dob,
              ssn,
              zipCode,
              phoneNumber,
              formatWorkLocations(workCity, workCountry, workState));
      call.enqueue(
          new Callback<>() {
            @Override
            public void onResponse(
                @NonNull Call<List<DenariiResponse>> call,
                @NonNull Response<List<DenariiResponse>> response) {
              if (response.isSuccessful()) {
                if (response.body() != null) {
                  UnpackDenariiResponse.unpackVerifyIdentity(finalUserDetails[0], response.body());

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

  private void formatStatus() {
    TextView status = findViewById(R.id.verificationStatus);

    DenariiUser denariiUser = userDetails.getDenariiUser();
    if (Objects.equals(denariiUser.getVerificationReportStatus(), "is_verified")) {
      status.setText(R.string.verification_verified_status);
      hideAllVerificationObjects();
    } else if (Objects.equals(denariiUser.getVerificationReportStatus(), "failed_verification")) {
      status.setText(R.string.verification_failed_status);
    } else if (Objects.equals(denariiUser.getVerificationReportStatus(), "verification_pending")) {
      status.setText(R.string.verification_pending_status);
    } else if (Objects.equals(denariiUser.getVerificationReportStatus(), "is_not_verified")) {
      status.setText(R.string.verification_not_yet_status);
    } else {
      status.setText(R.string.verification_unknown_status);
    }
  }

  private String formatWorkLocations(String workCity, String workCountry, String workState) {
    return String.format(
        "[{'country': %s, 'state': %s, 'city': %s}]", workCountry, workState, workCity);
  }

  private void hideAllVerificationObjects() {
    TextView firstNameTextView = findViewById(R.id.firstName);
    firstNameTextView.setVisibility(View.INVISIBLE);

    TextView middleInitialTextView = findViewById(R.id.middleInitial);
    middleInitialTextView.setVisibility(View.INVISIBLE);

    TextView lastNameTextView = findViewById(R.id.lastName);
    lastNameTextView.setVisibility(View.INVISIBLE);

    TextView emailTextView = findViewById(R.id.email);
    emailTextView.setVisibility(View.INVISIBLE);

    TextView dobTextView = findViewById(R.id.dateOfBirth);
    dobTextView.setVisibility(View.INVISIBLE);

    TextView ssnTextView = findViewById(R.id.socialSecurityNumber);
    ssnTextView.setVisibility(View.INVISIBLE);

    TextView zipCodeTextView = findViewById(R.id.zipCode);
    zipCodeTextView.setVisibility(View.INVISIBLE);

    TextView phoneNumberTextView = findViewById(R.id.verificationPhoneNumber);
    phoneNumberTextView.setVisibility(View.INVISIBLE);

    TextView workCityTextView = findViewById(R.id.workCity);
    workCityTextView.setVisibility(View.INVISIBLE);

    TextView workCountryTextView = findViewById(R.id.workCountry);
    workCountryTextView.setVisibility(View.INVISIBLE);

    TextView workStateTextView = findViewById(R.id.workState);
    workStateTextView.setVisibility(View.INVISIBLE);

    Button submitButton = findViewById(R.id.verificationSubmit);
    submitButton.setVisibility(View.INVISIBLE);
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
      Intent intent = new Intent(Verification.this, BuyDenarii.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.sell_denarii)) {
      Intent intent = new Intent(Verification.this, SellDenarii.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.wallet)) {
      Intent intent = new Intent(Verification.this, OpenedWallet.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.verification)) {
      return true;
    } else if (Objects.equals(item.getItemId(), R.id.credit_card_info)) {
      Intent intent = new Intent(Verification.this, CreditCardInfo.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.settings)) {
      Intent intent = new Intent(Verification.this, UserSettings.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  private void createToast(String message) {
    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;

    Toast toast = Toast.makeText(context, message, duration);
    toast.show();
  }
}
