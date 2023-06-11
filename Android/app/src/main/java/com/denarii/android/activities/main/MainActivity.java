package com.denarii.android.activities.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.denarii.android.R;
import com.denarii.android.activities.loginorregister.LoginOrRegister;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button next = (Button)findViewById(R.id.main_next);

        next.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginOrRegister.class)));
    }
}