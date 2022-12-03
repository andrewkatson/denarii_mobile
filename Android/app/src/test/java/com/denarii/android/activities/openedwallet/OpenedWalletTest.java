package com.denarii.android.activities.openedwallet;

import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

import com.denarii.android.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class OpenedWalletTest {
    @Test
    public void clickSend_doesNotTakeToAnyActivity() {
        try(ActivityController<OpenedWallet> controller = Robolectric.buildActivity(OpenedWallet.class)) {
            controller.setup();

            OpenedWallet activity = controller.get();

            activity.findViewById(R.id.opened_wallet_attempt_send_button).performClick();

            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertNull(actual);
        }
    }
}
