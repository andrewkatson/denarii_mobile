package com.denarii.android.testing;

import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.widget.EditText;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class ViewActionDenarii {

  // For an EditText with inputType="number" or inputType="phone", Espresso's typeText() doesn't
  // have any impact when run on robolectric.
  public static ViewAction forceTypeText(String text) {
    return new ViewAction() {
      @Override
      public String getDescription() {
        return "force type text";
      }

      @Override
      public Matcher<View> getConstraints() {
        return allOf(isEnabled());
      }

      @Override
      public void perform(UiController uiController, View view) {
        EditText editText = (EditText) view;
        editText.append(text);
        uiController.loopMainThreadUntilIdle();
      }
    };
  }
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
