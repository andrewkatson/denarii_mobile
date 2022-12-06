package com.denarii.android.activities.verifyreset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

import com.denarii.android.R;
import com.denarii.android.activities.resetpassword.ResetPassword;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class VerifyResetTest {

    @Test
    public void clickVerifyReset_doesNothing() {
        try(ActivityController<VerifyReset> controller = Robolectric.buildActivity(VerifyReset.class)) {
            controller.setup();

            VerifyReset activity = controller.get();

            activity.findViewById(R.id.verify_reset_verify_reset_button).performClick();

            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertNull(actual);
        }
    }

    @Test
    public void clickNext_takesToResetPasswordActivity() {
        try(ActivityController<VerifyReset> controller = Robolectric.buildActivity(VerifyReset.class)) {
            controller.setup();

            VerifyReset activity = controller.get();

            activity.findViewById(R.id.verify_reset_next_button).performClick();

            Intent expected = new Intent(activity, ResetPassword.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expected.getComponent(), actual.getComponent());
        }
    }
}
