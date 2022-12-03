package com.denarii.android.activities.walletdecision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

import com.denarii.android.R;
import com.denarii.android.activities.createwallet.CreateWallet;
import com.denarii.android.activities.openwallet.OpenWallet;
import com.denarii.android.activities.restorewallet.RestoreDeterministicWallet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class WalletDecisionTest {

    @Test
    public void clickCreateWallet_doesTakeToCreateWalletActivity() {
        try(ActivityController<WalletDecision> controller = Robolectric.buildActivity(WalletDecision.class)) {
            controller.setup();

            WalletDecision activity = controller.get();

            activity.findViewById(R.id.wallet_decision_create_wallet_button).performClick();

            Intent expectedIntent = new Intent(activity, CreateWallet.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        }
    }

    @Test
    public void clickOpenWallet_doesTakeToOpenWalletActivity() {
        try(ActivityController<WalletDecision> controller = Robolectric.buildActivity(WalletDecision.class)) {
            controller.setup();

            WalletDecision activity = controller.get();

            activity.findViewById(R.id.wallet_decision_open_wallet_button).performClick();

            Intent expectedIntent = new Intent(activity, OpenWallet.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        }
    }

    @Test
    public void clickRestoreWallet_doesTakeToRestoreWalletActivity() {
        try(ActivityController<WalletDecision> controller = Robolectric.buildActivity(WalletDecision.class)) {
            controller.setup();

            WalletDecision activity = controller.get();

            activity.findViewById(R.id.wallet_decision_restore_wallet_button).performClick();

            Intent expectedIntent = new Intent(activity, RestoreDeterministicWallet.class);
            Intent actual = shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actual.getComponent());
        }
    }
}
