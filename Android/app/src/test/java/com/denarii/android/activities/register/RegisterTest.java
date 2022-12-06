package com.denarii.android.activities.register;

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
public class RegisterTest {

    @Test
    public void clickSubmit_doesNothing() {
        try(ActivityController<Register> controller = Robolectric.buildActivity(Register.class)) {
            controller.setup();

            Register activity = controller.get();

            activity.findViewById(R.id.register_submit_button).performClick();

            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertNull(actual);
        }
    }

    @Test
    public void clickNext_takesToLoginActivity() {
        try(ActivityController<Register> controller = Robolectric.buildActivity(Register.class)) {
            controller.setup();

            Register activity = controller.get();

            activity.findViewById(R.id.register_next_button).performClick();

            Intent expected = new Intent(activity, Login.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expected.getComponent(), actual.getComponent());
        }
    }
}
