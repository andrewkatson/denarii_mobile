package com.denarii.android.util;

import android.widget.EditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputPatternValidator {

  public static boolean isValidInput(EditText text, String patternStr) {
    Pattern pattern = Pattern.compile(patternStr);
    return isValidInput(text, pattern);
  }

  public static boolean isValidInput(EditText text, Pattern pattern) {
    Matcher matcher = pattern.matcher(text.getText().toString());
    return matcher.matches();
  }
}
