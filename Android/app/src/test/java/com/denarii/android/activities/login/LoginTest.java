package com.denarii.android.activities.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

import com.denarii.android.R;
import com.denarii.android.activities.requestreset.RequestReset;
import com.denarii.android.activities.walletdecision.WalletDecision;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class LoginTest {

    @Test
    public void clickSubmit_doesNothing() {
        try(ActivityController<Login> controller = Robolectric.buildActivity(Login.class)) {
            controller.setup();

            Login activity = controller.get();

            activity.findViewById(R.id.login_submit_button).performClick();

            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertNull(actual);
        }
    }

    @Test
    public void clickNext_takesToWalletDecisionActivity() {
        try(ActivityController<Login> controller = Robolectric.buildActivity(Login.class)) {
            controller.setup();

            Login activity = controller.get();

            activity.findViewById(R.id.login_next_button).performClick();

            Intent expected = new Intent(activity, WalletDecision.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expected.getComponent(), actual.getComponent());
        }
    }

    @Test
    public void clickForgotPassword_takesToRequestResetActivity() {
        try(ActivityController<Login> controller = Robolectric.buildActivity(Login.class)) {
            controller.setup();

            Login activity = controller.get();

            activity.findViewById(R.id.login_forgot_password_button).performClick();

            Intent expected = new Intent(activity, RequestReset.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expected.getComponent(), actual.getComponent());
        }
    }
}
