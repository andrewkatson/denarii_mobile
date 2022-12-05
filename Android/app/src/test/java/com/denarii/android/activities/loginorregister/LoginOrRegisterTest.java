package com.denarii.android.activities.loginorregister;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

import com.denarii.android.R;
import com.denarii.android.activities.login.Login;
import com.denarii.android.activities.register.Register;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class LoginOrRegisterTest {
    @Test
    public void clickRegister_takesToRegisterActivity() {
        try(ActivityController<LoginOrRegister> controller = Robolectric.buildActivity(LoginOrRegister.class)) {
            controller.setup();

            LoginOrRegister activity = controller.get();

            activity.findViewById(R.id.login_or_register_register_button).performClick();

            Intent expectedIntent = new Intent(activity, Register.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        }
    }

    @Test
    public void clickLogin_takesToLoginActivity() {
        try(ActivityController<LoginOrRegister> controller = Robolectric.buildActivity(LoginOrRegister.class)) {
            controller.setup();

            LoginOrRegister activity = controller.get();

            activity.findViewById(R.id.login_or_register_login_button).performClick();

            Intent expectedIntent = new Intent(activity, Login.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        }
    }
}
