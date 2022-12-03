package com.denarii.android.activities.walletdecision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.denarii.android.R;
import com.denarii.android.activities.createwallet.CreateWallet;
import com.denarii.android.activities.openwallet.OpenWallet;
import com.denarii.android.activities.restorewallet.RestoreDeterministicWallet;
import com.denarii.android.constants.Constants;
import com.denarii.android.user.UserDetails;

public class WalletDecision extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_decision);

        setupButtons();
    }

    private void setupButtons() {
        Button openWallet = (Button) findViewById(R.id.wallet_decision_open_wallet_button);

        openWallet.setOnClickListener(v -> {
            Intent intent = new Intent(WalletDecision.this, OpenWallet.class);
            passAlongUserDetails(intent);
            startActivity(intent);
        });

        Button restoreWallet = (Button) findViewById(R.id.wallet_decision_restore_wallet_button);

        restoreWallet.setOnClickListener(v -> {
            Intent intent = new Intent(WalletDecision.this, RestoreDeterministicWallet.class);
            passAlongUserDetails(intent);
            startActivity(intent);
        });

        Button createWallet = (Button) findViewById(R.id.wallet_decision_create_wallet_button);

        createWallet.setOnClickListener(v -> {
            Intent intent = new Intent(WalletDecision.this, CreateWallet.class);
            passAlongUserDetails(intent);
            startActivity(intent);
        });
    }

    private void passAlongUserDetails(Intent intent) {
        intent.putExtra(Constants.USER_DETAILS, (UserDetails) getIntent().getSerializableExtra(Constants.USER_DETAILS));
    }
}