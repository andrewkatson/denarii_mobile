package com.denarii.android.activities.supportticketdetails;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.denarii.android.R;
import com.denarii.android.activities.buydenarii.BuyDenarii;
import com.denarii.android.activities.creditcardinfo.CreditCardInfo;
import com.denarii.android.activities.openedwallet.OpenedWallet;
import com.denarii.android.activities.selldenarii.SellDenarii;
import com.denarii.android.activities.supporttickets.SupportTickets;
import com.denarii.android.activities.usersettings.UserSettings;
import com.denarii.android.activities.verification.Verification;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.ui.models.SupportTicketCommentModel;
import com.denarii.android.ui.recyclerviewadapters.SupportTicketCommentRecyclerViewAdapter;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.SupportTicketComment;
import com.denarii.android.user.UserDetails;
import com.denarii.android.util.DenariiServiceHandler;
import com.denarii.android.util.UnpackDenariiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportTicketDetails extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private final ReentrantLock reentrantLock = new ReentrantLock();

    private DenariiService denariiService;

    private UserDetails userDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_ticket_details);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.support_ticket_details_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        denariiService = DenariiServiceHandler.returnDenariiService();

        Intent currentIntent = getIntent();
        userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);

        getSupportTicketDetails();
        getSupportTicketCommentDetails();

        Button addNewCommentButton = findViewById(R.id.commentBoxSubmit);

        addNewCommentButton.setOnClickListener(
                v -> {
                    addNewComment();
                });

        Button resolveButton = findViewById(R.id.resolveSupportTicket);

        resolveButton.setOnClickListener(
                v -> {
                    resolveSupportTicket();
                });

        Button deleteButton = findViewById(R.id.deleteSupportTicket);

        deleteButton.setOnClickListener(
                v -> {
                    deleteSupportTicket();
                });

        // Create the recycler view for the comments linear layout
        RecyclerView commentsRecyclerView = (RecyclerView) findViewById(R.id.supportTicketCommentsRecyclerView);

        SupportTicketCommentRecyclerViewAdapter commentAdapter = new SupportTicketCommentRecyclerViewAdapter(getComments(), userDetails.getUserName());
        commentsRecyclerView.setAdapter(commentAdapter);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onRefresh() {
        getSupportTicketCommentDetails();
    }

    private List<SupportTicketCommentModel> getComments() {
        List<SupportTicketCommentModel> commentModels = new ArrayList<>();

        for (SupportTicketComment comment : userDetails.getCurrentTicket().getSupportTicketCommentList()) {
            commentModels.add(new SupportTicketCommentModel(comment.getAuthor(), comment.getContent()));
        }

        return commentModels;
    }

    private void addNewComment() {
        try {
            reentrantLock.lock();

            if (Objects.equals(userDetails, null)) {
                userDetails = UnpackDenariiResponse.validUserDetails();
            }

            final UserDetails finalDetails = userDetails;

            EditText commentEditText = findViewById(R.id.supportTicketDetailsCommentBox);
            String comment = commentEditText.getText().toString();

            Call<List<DenariiResponse>> call =
                    denariiService.updateSupportTicket(
                            userDetails.getUserID(), userDetails.getCurrentTicket().getSupportID(), comment);
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
                                            UnpackDenariiResponse.unpackUpdateSupportTicket(
                                                    finalDetails, response.body());

                                    if (succeeded[0]) {
                                        createToast("Added comment");

                                        getSupportTicketCommentDetails();
                                    } else {
                                        createToast("Failed to add comment");
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

    private void getSupportTicketDetails() {
        try {
            reentrantLock.lock();

            if (Objects.equals(userDetails, null)) {
                userDetails = UnpackDenariiResponse.validUserDetails();
            }

            final UserDetails finalDetails = userDetails;

            Call<List<DenariiResponse>> call =
                    denariiService.getSupportTicket(
                            userDetails.getUserID(), userDetails.getCurrentTicket().getSupportID());
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
                                            UnpackDenariiResponse.unpackGetSupportTicket(finalDetails, response.body());

                                    if (succeeded[0]) {
                                        TextView supportTicketTitle = findViewById(R.id.supportTicketTitle);
                                        supportTicketTitle.setText(finalDetails.getCurrentTicket().getTitle());

                                        TextView supportTicketDescription = findViewById(R.id.supportTicketDescription);
                                        supportTicketDescription.setText(
                                                finalDetails.getCurrentTicket().getDescription());
                                    } else {
                                        createToast("Failed to fetch support ticket");
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

    private void getSupportTicketCommentDetails() {

        try {
            reentrantLock.lock();

            if (Objects.equals(userDetails, null)) {
                userDetails = UnpackDenariiResponse.validUserDetails();
            }

            final UserDetails finalDetails = userDetails;

            Call<List<DenariiResponse>> call =
                    denariiService.getCommentsOnTicket(
                            userDetails.getUserID(), userDetails.getCurrentTicket().getSupportID());
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
                                            UnpackDenariiResponse.unpackGetCommentsOnTicket(
                                                    finalDetails, response.body());

                                    if (succeeded[0]) {

                                    } else {
                                        createToast("Failed to fetch the comments");
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

    private void resolveSupportTicket() {
        try {
            reentrantLock.lock();

            if (Objects.equals(userDetails, null)) {
                userDetails = UnpackDenariiResponse.validUserDetails();
            }

            Call<List<DenariiResponse>> call =
                    denariiService.resolveSupportTicket(
                            userDetails.getUserID(), userDetails.getCurrentTicket().getSupportID());
            final boolean[] succeeded = {false};
            call.enqueue(
                    new Callback<>() {
                        @Override
                        public void onResponse(
                                @NonNull Call<List<DenariiResponse>> call,
                                @NonNull Response<List<DenariiResponse>> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    succeeded[0] = UnpackDenariiResponse.unpackResolveSupportTicket(response.body());

                                    if (succeeded[0]) {
                                        createToast("Resolved ticket!");

                                        Intent intent = new Intent(SupportTicketDetails.this, SupportTickets.class);

                                        intent.putExtra(Constants.USER_DETAILS, userDetails);

                                        startActivity(intent);
                                    } else {
                                        createToast("Failed to resolve ticket");
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

    private void deleteSupportTicket() {
        try {
            reentrantLock.lock();

            if (Objects.equals(userDetails, null)) {
                userDetails = UnpackDenariiResponse.validUserDetails();
            }

            Call<List<DenariiResponse>> call =
                    denariiService.deleteSupportTicket(
                            userDetails.getUserID(), userDetails.getCurrentTicket().getSupportID());
            final boolean[] succeeded = {false};
            call.enqueue(
                    new Callback<>() {
                        @Override
                        public void onResponse(
                                @NonNull Call<List<DenariiResponse>> call,
                                @NonNull Response<List<DenariiResponse>> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    succeeded[0] = UnpackDenariiResponse.unpackDeleteSupportTicket(response.body());

                                    if (succeeded[0]) {
                                        createToast("Deleted ticket!");

                                        Intent intent = new Intent(SupportTicketDetails.this, SupportTickets.class);

                                        intent.putExtra(Constants.USER_DETAILS, userDetails);

                                        startActivity(intent);
                                    } else {
                                        createToast("Failed to delete ticket");
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
            Intent intent = new Intent(SupportTicketDetails.this, BuyDenarii.class);

            intent.putExtra(Constants.USER_DETAILS, userDetails);

            startActivity(intent);

            return true;
        } else if (Objects.equals(item.getItemId(), R.id.sell_denarii)) {
            Intent intent = new Intent(SupportTicketDetails.this, SellDenarii.class);

            intent.putExtra(Constants.USER_DETAILS, userDetails);

            startActivity(intent);

            return true;
        } else if (Objects.equals(item.getItemId(), R.id.wallet)) {
            Intent intent = new Intent(SupportTicketDetails.this, OpenedWallet.class);

            intent.putExtra(Constants.USER_DETAILS, userDetails);

            startActivity(intent);

            return true;
        } else if (Objects.equals(item.getItemId(), R.id.verification)) {
            Intent intent = new Intent(SupportTicketDetails.this, Verification.class);

            intent.putExtra(Constants.USER_DETAILS, userDetails);

            startActivity(intent);

            return true;
        } else if (Objects.equals(item.getItemId(), R.id.credit_card_info)) {
            Intent intent = new Intent(SupportTicketDetails.this, CreditCardInfo.class);

            intent.putExtra(Constants.USER_DETAILS, userDetails);

            startActivity(intent);

            return true;
        } else if (Objects.equals(item.getItemId(), R.id.settings)) {
            Intent intent = new Intent(SupportTicketDetails.this, UserSettings.class);

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
