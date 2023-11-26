package com.denarii.android.activities.verification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.denarii.android.R;
import com.denarii.android.activities.buydenarii.BuyDenarii;
import com.denarii.android.activities.creditcardinfo.CreditCardInfo;
import com.denarii.android.activities.openedwallet.OpenedWallet;
import com.denarii.android.activities.selldenarii.SellDenarii;
import com.denarii.android.activities.usersettings.UserSettings;
import com.denarii.android.constants.Constants;
import com.denarii.android.user.UserDetails;

import java.util.Objects;

public class Verification extends AppCompatActivity {

    private UserDetails userDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        Intent currentIntent = getIntent();
        userDetails = (UserDetails) currentIntent.getSerializableExtra(Constants.USER_DETAILS);
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
}