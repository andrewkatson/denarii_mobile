package com.denarii.android.activities.openwallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

import com.denarii.android.R;
import com.denarii.android.activities.openedwallet.OpenedWallet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class OpenWalletTest {
    @Test
    public void clickSubmit_doesNotTakeToOpenedWalletActivity() {
        try(ActivityController<OpenWallet> controller = Robolectric.buildActivity(OpenWallet.class)) {
            controller.setup();

            OpenWallet activity = controller.get();

            activity.findViewById(R.id.open_wallet_submit_button).performClick();

            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertNull(actual);
        }
    }

    @Test
    public void clickNext_takesToOpenedWalletActivity() {
        try(ActivityController<OpenWallet> controller = Robolectric.buildActivity(OpenWallet.class)) {
            controller.setup();

            OpenWallet activity = controller.get();

            activity.findViewById(R.id.open_wallet_next_button).performClick();

            Intent expected = new Intent(activity, OpenedWallet.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expected.getComponent(), actual.getComponent());
        }
    }
}
