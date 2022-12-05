package com.denarii.android.activities.loginorregister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.denarii.android.R;
import com.denarii.android.activities.login.Login;
import com.denarii.android.activities.register.Register;

public class LoginOrRegister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        Button register = (Button) findViewById(R.id.login_or_register_register_button);

        register.setOnClickListener(v -> {
            startActivity(new Intent(LoginOrRegister.this, Register.class));
        });

        Button login = (Button) findViewById(R.id.login_or_register_login_button);

        login.setOnClickListener(
                v -> {
                    startActivity(new Intent(LoginOrRegister.this, Login.class));
                }
        );
    }
}