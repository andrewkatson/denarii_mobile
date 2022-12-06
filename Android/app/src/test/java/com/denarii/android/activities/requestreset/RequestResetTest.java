package com.denarii.android.activities.requestreset;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

import com.denarii.android.R;
import com.denarii.android.activities.verifyreset.VerifyReset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class RequestResetTest {
    @Test
    public void clickRequestReset_doesNothing() {
        try(ActivityController<RequestReset> controller = Robolectric.buildActivity(RequestReset.class)) {
            controller.setup();

            RequestReset activity = controller.get();

            activity.findViewById(R.id.request_reset_request_reset_button).performClick();

            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertNull(actual);
        }
    }

    @Test
    public void clickNext_takesToVerifyResetActivity() {
        try(ActivityController<RequestReset> controller = Robolectric.buildActivity(RequestReset.class)) {
            controller.setup();

            RequestReset activity = controller.get();

            activity.findViewById(R.id.request_reset_next_button).performClick();

            Intent expected = new Intent(activity, VerifyReset.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expected.getComponent(), actual.getComponent());
        }
    }
}
