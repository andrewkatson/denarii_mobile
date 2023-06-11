package com.denarii.android.navigation;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.runner.lifecycle.Stage.RESUMED;

import android.app.Activity;

import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

import org.junit.Test;

import java.util.Collection;

public class ComplexNavigationTest {
    private Activity currentActivity;

    public Activity getActivityInstance(){
        getInstrumentation().runOnMainSync(() -> {
            Collection<Activity> resumedActivities =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
            if (resumedActivities.iterator().hasNext()){
                currentActivity = resumedActivities.iterator().next();
            }
        });

        return currentActivity;
    }

    @Test
    public void loginWithDenarii() {

    }

    @Test
    public void registerWithDenarii() {

    }

    @Test
    public void resetPassword() {

    }

    @Test
    public void getBalance() {

    }

    @Test
    public void sendDenarii() {

    }
}
