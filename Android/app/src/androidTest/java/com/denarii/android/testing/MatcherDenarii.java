package com.denarii.android.testing;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class MatcherDenarii {
    public static Matcher<View> hasValueEqualTo(final String content) {

        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("Has EditText/TextView the value:  " + content);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextView) && !(view instanceof EditText)) {
                    return false;
                }
                String text;
                if (view instanceof TextView) {
                    text = ((TextView) view).getText().toString();
                } else {
                    text = ((EditText) view).getText().toString();
                }

                // Throw an exception if it does not match so we can catch it
                boolean matches = text.equalsIgnoreCase(content);
                if (matches) {
                    return matches;
                }
                throw new IllegalArgumentException(String.format("The value %s does not match text view: %s!", content, text));
            }
        };
    }
}
