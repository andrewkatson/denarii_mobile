package com.denarii.android.activities.supporttickets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.denarii.android.R;
import com.denarii.android.activities.buydenarii.BuyDenarii;
import com.denarii.android.activities.createsupportticket.CreateSupportTicket;
import com.denarii.android.activities.creditcardinfo.CreditCardInfo;
import com.denarii.android.activities.openedwallet.OpenedWallet;
import com.denarii.android.activities.selldenarii.SellDenarii;
import com.denarii.android.activities.supportticketdetails.SupportTicketDetails;
import com.denarii.android.activities.usersettings.UserSettings;
import com.denarii.android.activities.verification.Verification;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.ui.containers.DenariiSupportTicketArtifacts;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.SupportTicket;
import com.denarii.android.user.UserDetails;
import com.denarii.android.util.DenariiServiceHandler;
import com.denarii.android.util.UnpackDenariiResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportTickets extends AppCompatActivity
    implements SwipeRefreshLayout.OnRefreshListener {

  private final ReentrantLock reentrantLock = new ReentrantLock();

  private DenariiService denariiService;

  private final List<DenariiSupportTicketArtifacts> denariiSupportTicketsArtifactsList =
      new ArrayList<>();

  private UserDetails userDetails = null;

  private final Random random = new Random();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_support_ticket);

    // Find the toolbar view inside the activity layout
    Toolbar toolbar = (Toolbar) findViewById(R.id.support_ticket_toolbar);
    // Sets the Toolbar to act as the ActionBar for this Activity window.
    // Make sure the toolbar exists in the activity and is not null
    setSupportActionBar(toolbar);

    denariiService = DenariiServiceHandler.returnDenariiService();

    Intent currentIntent = getIntent();
    userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

    Button createSupportTicketButton = findViewById(R.id.createSupportTicket);

    createSupportTicketButton.setOnClickListener(
        v -> {
          Intent intent = new Intent(SupportTickets.this, CreateSupportTicket.class);

          intent.putExtra(Constants.USER_DETAILS, userDetails);

          startActivity(intent);
        });
  }

  @Override
  public void onRefresh() {
    getSupportTickets();
  }

  private void getSupportTickets() {
    try {
      reentrantLock.lock();

      if (Objects.equals(userDetails, null)) {
        userDetails = UnpackDenariiResponse.validUserDetails();
      }

      final UserDetails finalUserDetails = userDetails;

      // TODO: allow the user to request resolved tickets.
      Call<List<DenariiResponse>> call =
          denariiService.getSupportTickets(userDetails.getUserID(), "False");
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
                      UnpackDenariiResponse.unpackGetSupportTickets(
                          finalUserDetails, response.body());

                  if (succeeded[0]) {
                    updateList();
                  } else {
                    createToast("Failed to fetch support tickets.");
                  }
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

  private void updateList() {
    clearSupportTicketsList();
    updateSupportTicketsList();
  }

  private void clearSupportTicketsList() {
    for (DenariiSupportTicketArtifacts artifacts : denariiSupportTicketsArtifactsList) {
      artifacts.getSupportTicketButton().setVisibility(View.GONE);
    }
    denariiSupportTicketsArtifactsList.clear();
  }

  private void updateSupportTicketsList() {

    boolean firstArtifact = true;

    ConstraintLayout parentLayout = findViewById(R.id.supportTicketsLayout);

    // First we make all the artifacts
    for (SupportTicket supportTicket : userDetails.getSupportTicketList()) {
      DenariiSupportTicketArtifacts artifacts = new DenariiSupportTicketArtifacts();

      Button button = new Button(this);
      button.setId(random.nextInt(1000000));
      button.setOnClickListener(
          v -> {
            Intent intent = new Intent(SupportTickets.this, SupportTicketDetails.class);

            intent.putExtra(Constants.USER_DETAILS, userDetails);

            startActivity(intent);
          });

      artifacts.setSupportTicketButton(button);
      artifacts.setSupportTicket(supportTicket);

      denariiSupportTicketsArtifactsList.add(artifacts);
    }

    // Then we apply their constraints
    for (int i = 0; i < denariiSupportTicketsArtifactsList.size(); i++) {
      DenariiSupportTicketArtifacts artifacts = denariiSupportTicketsArtifactsList.get(i);
      Button button = artifacts.getSupportTicketButton();
      SupportTicket ticket = artifacts.getSupportTicket();
      String title = ticket.getTitle();

      ConstraintSet ticketConstraintSet = new ConstraintSet();
      ticketConstraintSet.clone(parentLayout);

      ticketConstraintSet.connect(
          button.getId(), ConstraintSet.RIGHT, parentLayout.getId(), ConstraintSet.RIGHT);
      ticketConstraintSet.connect(
          button.getId(), ConstraintSet.LEFT, parentLayout.getId(), ConstraintSet.LEFT);

      // If this is not the first artifact then we should connect the top to the previous artifact
      if (!firstArtifact) {
        DenariiSupportTicketArtifacts previousArtifact =
            denariiSupportTicketsArtifactsList.get(i - 1);
        Button previousButton = previousArtifact.getSupportTicketButton();
        ticketConstraintSet.connect(
            button.getId(), ConstraintSet.TOP, previousButton.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise connect it to the parent
        ticketConstraintSet.connect(
            button.getId(), ConstraintSet.TOP, parentLayout.getId(), ConstraintSet.TOP);
      }

      // If we are at the end of the list then constrain by the parent
      if (i == denariiSupportTicketsArtifactsList.size() - 1) {
        ticketConstraintSet.connect(
            button.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiSupportTicketArtifacts nextArtifact = denariiSupportTicketsArtifactsList.get(i + 1);
        Button nextButton = nextArtifact.getSupportTicketButton();
        ticketConstraintSet.connect(
            button.getId(), ConstraintSet.BOTTOM, nextButton.getId(), ConstraintSet.TOP);
      }
      ticketConstraintSet.applyTo(parentLayout);

      firstArtifact = false;
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
      Intent intent = new Intent(SupportTickets.this, BuyDenarii.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.sell_denarii)) {
      Intent intent = new Intent(SupportTickets.this, SellDenarii.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.wallet)) {
      Intent intent = new Intent(SupportTickets.this, OpenedWallet.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.verification)) {
      Intent intent = new Intent(SupportTickets.this, Verification.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.credit_card_info)) {
      Intent intent = new Intent(SupportTickets.this, CreditCardInfo.class);

      intent.putExtra(Constants.USER_DETAILS, userDetails);

      startActivity(intent);

      return true;
    } else if (Objects.equals(item.getItemId(), R.id.settings)) {
      Intent intent = new Intent(SupportTickets.this, UserSettings.class);

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
