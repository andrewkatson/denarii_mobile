package com.denarii.android.activities.userinfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

import com.denarii.android.R;
import com.denarii.android.activities.walletdecision.WalletDecision;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class UserInfoTest {
    @Test
    public void clickSubmit_doesNotTakeToWalletDecisionActivity() {
        try(ActivityController<UserInfo> controller = Robolectric.buildActivity(UserInfo.class)) {
            controller.setup();

            UserInfo activity = controller.get();

            activity.findViewById(R.id.user_info_submit).performClick();

            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertNull(actual);
        }
    }

    @Test
    public void clickNext_doesNotTakeToWalletDecisionActivity() {
        try(ActivityController<UserInfo> controller = Robolectric.buildActivity(UserInfo.class)) {
            controller.setup();

            UserInfo activity = controller.get();

            activity.findViewById(R.id.user_info_next).performClick();

            Intent expectedIntent = new Intent(activity, WalletDecision.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        }
    }
}
