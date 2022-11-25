package com.denarii.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WalletDecision extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_decision);

        setupButtons();
    }

    private void setupButtons() {
        Button openWallet = (Button) findViewById(R.id.open_wallet_button);

        openWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WalletDecision.this, OpenWallet.class));
            }
        });

        Button restoreWallet = (Button) findViewById(R.id.restore_wallet_button);

        restoreWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WalletDecision.this, RestoreDeterministicWallet.class));
            }
        });

        Button createWallet = (Button) findViewById(R.id.create_wallet_button);

        createWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WalletDecision.this, CreateWallet.class));
            }
        });
    }
}