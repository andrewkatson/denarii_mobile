package com.denarii.android.activities.supportticketdetails;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
import com.denarii.android.ui.containers.DenariiCommentArtifacts;
import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.SupportTicketComment;
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

public class SupportTicketDetails extends AppCompatActivity
    implements SwipeRefreshLayout.OnRefreshListener {

  private final ReentrantLock reentrantLock = new ReentrantLock();

  private DenariiService denariiService;

  private final List<DenariiCommentArtifacts> denariiCommentArtifactsList = new ArrayList<>();

  private UserDetails userDetails = null;

  private Random random = new Random();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_support_ticket_details);

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
  }

  @Override
  public void onRefresh() {
    getSupportTicketCommentDetails();
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
                    updateList();
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

  private void updateList() {
    clearDenariiCommentArtifacts();
    updateDenariiCommentArtifacts();
  }

  private void clearDenariiCommentArtifacts() {
    for (DenariiCommentArtifacts artifact : denariiCommentArtifactsList) {
      artifact.getBodyTextView().setVisibility(View.GONE);
    }
  }

  private void updateDenariiCommentArtifacts() {

    boolean firstArtifact = true;

    ConstraintLayout parentLayout = findViewById(R.id.commentsLayout);

    // First we make all the artifacts
    for (SupportTicketComment comment :
        userDetails.getCurrentTicket().getSupportTicketCommentList()) {
      DenariiCommentArtifacts artifacts = new DenariiCommentArtifacts();

      TextView commentTextView = new TextView(this);
      commentTextView.setId(random.nextInt(1000000));
      commentTextView.setText(comment.getContent());

      artifacts.setBodyTextView(commentTextView);
      artifacts.setSupportTicketComment(comment);

      denariiCommentArtifactsList.add(artifacts);
    }

    // Then we apply their constraints
    for (int i = 0; i < denariiCommentArtifactsList.size(); i++) {
      DenariiCommentArtifacts artifacts = denariiCommentArtifactsList.get(i);
      TextView commentText = artifacts.getBodyTextView();
      SupportTicketComment comment = artifacts.getSupportTicketComment();

      boolean isAuthoredByCurrentUser =
          Objects.equals(comment.getAuthor(), userDetails.getUserName());

      ConstraintSet commentConstraintSet = new ConstraintSet();
      commentConstraintSet.clone(parentLayout);

      // If this is authored by the current user have it appear on the right side
      if (isAuthoredByCurrentUser) {
        commentConstraintSet.connect(
            commentText.getId(), ConstraintSet.RIGHT, parentLayout.getId(), ConstraintSet.RIGHT);
      } else {
        commentConstraintSet.connect(
            commentText.getId(), ConstraintSet.LEFT, parentLayout.getId(), ConstraintSet.LEFT);
      }

      // If this is not the first artifact then we should connect the top to the previous artifact
      if (!firstArtifact) {
        DenariiCommentArtifacts previousArtifact = denariiCommentArtifactsList.get(i - 1);
        TextView previousCommentText = previousArtifact.getBodyTextView();
        commentConstraintSet.connect(
            commentText.getId(),
            ConstraintSet.TOP,
            previousCommentText.getId(),
            ConstraintSet.BOTTOM);
      } else {
        // Otherwise connect it to the parent
        commentConstraintSet.connect(
            commentText.getId(), ConstraintSet.TOP, parentLayout.getId(), ConstraintSet.TOP);
      }

      // If we are at the end of the list then constrain by the parent
      if (i == denariiCommentArtifactsList.size() - 1) {
        commentConstraintSet.connect(
            commentText.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
      } else {
        // Otherwise constrain by the next artifact
        DenariiCommentArtifacts nextArtifact = denariiCommentArtifactsList.get(i + 1);
        TextView nextCommentText = nextArtifact.getBodyTextView();
        commentConstraintSet.connect(
            commentText.getId(), ConstraintSet.BOTTOM, nextCommentText.getId(), ConstraintSet.TOP);
      }
      commentConstraintSet.applyTo(parentLayout);

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
