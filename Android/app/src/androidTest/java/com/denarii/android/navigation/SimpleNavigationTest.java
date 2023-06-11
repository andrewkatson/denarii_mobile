package com.denarii.android.navigation;


import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.denarii.android.R;
import com.denarii.android.activities.createwallet.CreateWallet;
import com.denarii.android.activities.login.Login;
import com.denarii.android.activities.loginorregister.LoginOrRegister;
import com.denarii.android.activities.main.MainActivity;
import com.denarii.android.activities.openedwallet.OpenedWallet;
import com.denarii.android.activities.openwallet.OpenWallet;
import com.denarii.android.activities.register.Register;
import com.denarii.android.activities.requestreset.RequestReset;
import com.denarii.android.activities.resetpassword.ResetPassword;
import com.denarii.android.activities.restorewallet.RestoreDeterministicWallet;
import com.denarii.android.activities.verifyreset.VerifyReset;
import com.denarii.android.activities.walletdecision.WalletDecision;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class SimpleNavigationTest {

    @Test
    public void toLoginOrRegisterFromMain() {
        try(ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                TextView welcome = activity.findViewById(R.id.welcome_text);
                assertEquals("Welcome to Denarii!", welcome.getText().toString());

                Button next = activity.findViewById(R.id.main_next);

                next.performClick();
            });
        }
    }

    @Test
    public void toLoginFromLoginOrRegister() {
        try(ActivityScenario<LoginOrRegister> scenario = ActivityScenario.launch(LoginOrRegister.class)) {
            scenario.onActivity(activity -> {

                Button login = activity.findViewById(R.id.login_or_register_login_button);

                login.performClick();
            });
        }
    }

    @Test
    public void toRegisterFromLoginOrRegister() {
        try(ActivityScenario<LoginOrRegister> scenario = ActivityScenario.launch(LoginOrRegister.class)) {
            scenario.onActivity(activity -> {

                Button register = activity.findViewById(R.id.login_or_register_register_button);

                register.performClick();
            });
        }
    }

    @Test
    public void loginwithDenarii() {
        try(ActivityScenario<Login> scenario = ActivityScenario.launch(Login.class)) {
            scenario.onActivity(activity -> {

                EditText name = activity.findViewById(R.id.login_enter_name_edit_text);
                name.setText("User");

                EditText email = activity.findViewById(R.id.login_enter_email_edit_text);
                email.setText("email@email.com");

                EditText password = activity.findViewById(R.id.login_enter_password_edit_text);
                password.setText("password");

                Button submit = activity.findViewById(R.id.login_submit_button);
                submit.performClick();

                Button next = activity.findViewById(R.id.login_next_button);
                assertEquals(View.VISIBLE, next.getVisibility());

                next.performClick();
            });
        }
    }

    @Test
    public void registerWithDenarii() {
        try(ActivityScenario<Register> scenario = ActivityScenario.launch(Register.class)) {
            scenario.onActivity(activity -> {

                EditText name = activity.findViewById(R.id.register_enter_name_edit_text);
                name.setText("User");

                EditText email = activity.findViewById(R.id.register_enter_email_edit_text);
                email.setText("email@email.com");

                EditText password = activity.findViewById(R.id.register_enter_password_edit_text);
                password.setText("password");

                Button submit = activity.findViewById(R.id.register_submit_button);
                submit.performClick();

                Button next = activity.findViewById(R.id.register_next_button);
                assertEquals(View.VISIBLE, next.getVisibility());

                next.performClick();
            });
        }
    }

    @Test
    public void requestReset() {
        try(ActivityScenario<RequestReset> scenario = ActivityScenario.launch(RequestReset.class)) {
            scenario.onActivity(activity -> {

                EditText nameOrEmail = activity.findViewById(R.id.request_reset_username_or_email_edit_text);
                nameOrEmail.setText("User");

                Button requestReset = activity.findViewById(R.id.request_reset_request_reset_button);
                requestReset.performClick();

                Button next = activity.findViewById(R.id.request_reset_next_button);
                assertEquals(View.VISIBLE, next.getVisibility());

                next.performClick();
            });
        }
    }

    @Test
    public void verifyReset() {
        try(ActivityScenario<VerifyReset> scenario = ActivityScenario.launch(VerifyReset.class)) {
            scenario.onActivity(activity -> {

                EditText resetId = activity.findViewById(R.id.verify_reset_reset_id_edit_text);
                resetId.setText("1243");

                Button verifyReset = activity.findViewById(R.id.verify_reset_verify_reset_button);
                verifyReset.performClick();

                Button next = activity.findViewById(R.id.verify_reset_next_button);
                assertEquals(View.VISIBLE, next.getVisibility());

                next.performClick();
            });
        }
    }

    @Test
    public void resetPassword() {
        try(ActivityScenario<ResetPassword> scenario = ActivityScenario.launch(ResetPassword.class)) {
            scenario.onActivity(activity -> {

                EditText name = activity.findViewById(R.id.reset_password_enter_name_edit_text);
                name.setText("user");

                EditText email = activity.findViewById(R.id.reset_password_enter_email_edit_text);
                email.setText("email@email.com");

                EditText password = activity.findViewById(R.id.reset_password_new_password_edit_text);
                password.setText("password");

                EditText confirmPassword = activity.findViewById(R.id.reset_password_confirm_password_edit_text);
                confirmPassword.setText("password");

                Button resetPassword = activity.findViewById(R.id.reset_password_reset_password_button);
                resetPassword.performClick();

                Button next = activity.findViewById(R.id.reset_password_next_button);
                assertEquals(View.VISIBLE, next.getVisibility());

                next.performClick();
            });
        }
    }

    @Test
    public void toCreateWalletFromWalletDecision() {
        try(ActivityScenario<WalletDecision> scenario = ActivityScenario.launch(WalletDecision.class)) {
            scenario.onActivity(activity -> {

                Button createWallet = activity.findViewById(R.id.wallet_decision_open_wallet_button);
                createWallet.performClick();

            });
        }
    }

    @Test
    public void toOpenWalletFromWalletDecision() {
        try(ActivityScenario<WalletDecision> scenario = ActivityScenario.launch(WalletDecision.class)) {
            scenario.onActivity(activity -> {

                Button openWallet = activity.findViewById(R.id.wallet_decision_open_wallet_button);
                openWallet.performClick();

            });
        }
    }

    @Test
    public void toRestoreWalletFromWalletDecision() {
        try(ActivityScenario<WalletDecision> scenario = ActivityScenario.launch(WalletDecision.class)) {
            scenario.onActivity(activity -> {

                Button restoreWallet = activity.findViewById(R.id.wallet_decision_restore_wallet_button);
                restoreWallet.performClick();

            });
        }
    }

    @Test
    public void createWallet() {
        try(ActivityScenario<CreateWallet> scenario = ActivityScenario.launch(CreateWallet.class)) {
            scenario.onActivity(activity -> {

                EditText walletName = activity.findViewById(R.id.create_wallet_enter_name);
                walletName.setText("wallet");

                EditText password = activity.findViewById(R.id.create_wallet_enter_password);
                password.setText("password");

                Button submit = activity.findViewById(R.id.create_wallet_submit_button);
                submit.performClick();

                Button next = activity.findViewById(R.id.create_wallet_next_button);
                assertEquals(View.VISIBLE, next.getVisibility());

                next.performClick();
            });
        }
    }

    @Test
    public void openWallet() {
        try(ActivityScenario<OpenWallet> scenario = ActivityScenario.launch(OpenWallet.class)) {
            scenario.onActivity(activity -> {

                EditText walletName = activity.findViewById(R.id.open_wallet_enter_name);
                walletName.setText("wallet");

                EditText password = activity.findViewById(R.id.open_wallet_enter_password);
                password.setText("password");

                Button submit = activity.findViewById(R.id.open_wallet_submit_button);
                submit.performClick();

                Button next = activity.findViewById(R.id.open_wallet_next_button);
                assertEquals(View.VISIBLE, next.getVisibility());

                next.performClick();
            });
        }
    }

    @Test
    public void restoreWallet() {
        try(ActivityScenario<RestoreDeterministicWallet> scenario = ActivityScenario.launch(RestoreDeterministicWallet.class)) {
            scenario.onActivity(activity -> {

                EditText walletName = activity.findViewById(R.id.restore_wallet_enter_name);
                walletName.setText("wallet");

                EditText password = activity.findViewById(R.id.restore_wallet_enter_password);
                password.setText("password");

                EditText seed = activity.findViewById(R.id.restore_wallet_enter_seed);
                seed.setText("some seed here");

                Button submit = activity.findViewById(R.id.restore_wallet_submit_button);
                submit.performClick();

                Button next = activity.findViewById(R.id.restore_wallet_next_button);
                assertEquals(View.VISIBLE, next.getVisibility());

                next.performClick();
            });
        }
    }

    @Test
    public void getBalance() {
        try(ActivityScenario<OpenedWallet> scenario = ActivityScenario.launch(OpenedWallet.class)) {
            scenario.onActivity(activity -> {
                Button refreshBalance = activity.findViewById(R.id.opened_wallet_refresh_balance_button);
                refreshBalance.performClick();

                TextView balance = activity.findViewById(R.id.opened_wallet_balance_text_view);
                assertEquals("20", balance.getText().toString());
            });
        }
    }

    @Test
    public void sendDenarii() {
        try(ActivityScenario<OpenedWallet> scenario = ActivityScenario.launch(OpenedWallet.class)) {
            scenario.onActivity(activity -> {

                EditText sendTo = activity.findViewById(R.id.opened_wallet_to_edit_text);
                sendTo.setText("XYZABC");

                EditText amount = activity.findViewById(R.id.opened_wallet_amount_edit_text);
                amount.setText("10");

                Button sendDenarii = activity.findViewById(R.id.opened_wallet_attempt_send_button);
                sendDenarii.performClick();
            });
        }
    }

}

