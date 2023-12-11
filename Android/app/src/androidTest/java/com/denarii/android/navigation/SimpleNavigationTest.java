package com.denarii.android.navigation;

import static org.junit.Assert.assertEquals;

import android.system.ErrnoException;
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
import com.denarii.android.constants.Constants;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SimpleNavigationTest {

  @BeforeClass
  public static void beforeClass() throws ErrnoException {
    Constants.DEBUG = true;
  }

  @Test
  public void toLoginOrRegisterFromMain() {
    try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
      scenario.onActivity(
          activity -> {
            TextView welcome = activity.findViewById(R.id.welcome_text);
            assertEquals("Welcome to Denarii!", welcome.getText().toString());

            Button next = activity.findViewById(R.id.main_next);

            next.performClick();
          });
    }
  }

  @Test
  public void toLoginFromLoginOrRegister() {
    try (ActivityScenario<LoginOrRegister> scenario =
        ActivityScenario.launch(LoginOrRegister.class)) {
      scenario.onActivity(
          activity -> {
            Button login = activity.findViewById(R.id.login_or_register_login_button);

            login.performClick();
          });
    }
  }

  @Test
  public void toRegisterFromLoginOrRegister() {
    try (ActivityScenario<LoginOrRegister> scenario =
        ActivityScenario.launch(LoginOrRegister.class)) {
      scenario.onActivity(
          activity -> {
            Button register = activity.findViewById(R.id.login_or_register_register_button);

            register.performClick();
          });
    }
  }

  @Test
  public void toCreateWalletFromWalletDecision() {
    try (ActivityScenario<WalletDecision> scenario =
        ActivityScenario.launch(WalletDecision.class)) {
      scenario.onActivity(
          activity -> {
            Button createWallet = activity.findViewById(R.id.wallet_decision_open_wallet_button);
            createWallet.performClick();
          });
    }
  }

  @Test
  public void toOpenWalletFromWalletDecision() {
    try (ActivityScenario<WalletDecision> scenario =
        ActivityScenario.launch(WalletDecision.class)) {
      scenario.onActivity(
          activity -> {
            Button openWallet = activity.findViewById(R.id.wallet_decision_open_wallet_button);
            openWallet.performClick();
          });
    }
  }

  @Test
  public void toRestoreWalletFromWalletDecision() {
    try (ActivityScenario<WalletDecision> scenario =
        ActivityScenario.launch(WalletDecision.class)) {
      scenario.onActivity(
          activity -> {
            Button restoreWallet =
                activity.findViewById(R.id.wallet_decision_restore_wallet_button);
            restoreWallet.performClick();
          });
    }
  }
}
