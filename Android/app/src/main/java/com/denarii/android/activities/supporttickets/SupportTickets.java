package com.denarii.android.activities.supporttickets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.SupportTicket;
import com.denarii.android.user.UserDetails;
import com.denarii.android.util.DenariiServiceHandler;
import com.denarii.android.util.UnpackDenariiResponse;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportTickets extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private final ReentrantLock reentrantLock = new ReentrantLock();

    private DenariiService denariiService;

    private UserDetails userDetails = null;

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

        getSupportTickets();
    }

    @Override
    public void onRefresh() {
        getSupportTickets();
    }

    private void navigateToSupportTicket(String supportTicketId) {
        Intent intent = new Intent(SupportTickets.this, SupportTicketDetails.class);

        SupportTicket currentTicket = new SupportTicket();
        currentTicket.setSupportID(supportTicketId);

        userDetails.setCurrentTicket(currentTicket);

        intent.putExtra(Constants.USER_DETAILS, userDetails);
        
        startActivity(intent);
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
