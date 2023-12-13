package com.denarii.android.testing;

import android.view.View;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

public class ViewActionDenarii {
  public static ViewAction withCustomConstraints(
      final ViewAction action, final org.hamcrest.Matcher<View> constraints) {
    return new ViewAction() {
      @Override
      public org.hamcrest.Matcher<View> getConstraints() {
        return constraints;
      }

      @Override
      public String getDescription() {
        return action.getDescription();
      }

      @Override
      public void perform(UiController uiController, View view) {
        action.perform(uiController, view);
      }
    };
  }
}
