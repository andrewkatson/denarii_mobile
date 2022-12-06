package com.denarii.android.activities.resetpassword;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

import com.denarii.android.R;
import com.denarii.android.activities.login.Login;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class ResetPasswordTest {

    @Test
    public void clickResetPassword_doesNothing() {
        try(ActivityController<ResetPassword> controller = Robolectric.buildActivity(ResetPassword.class)) {
            controller.setup();

            ResetPassword activity = controller.get();

            activity.findViewById(R.id.reset_password_reset_password_button).performClick();

            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertNull(actual);
        }
    }

    @Test
    public void clickNext_takesToLoginActivity() {
        try(ActivityController<ResetPassword> controller = Robolectric.buildActivity(ResetPassword.class)) {
            controller.setup();

            ResetPassword activity = controller.get();

            activity.findViewById(R.id.reset_password_next_button).performClick();

            Intent expected = new Intent(activity, Login.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expected.getComponent(), actual.getComponent());
        }
    }
}
