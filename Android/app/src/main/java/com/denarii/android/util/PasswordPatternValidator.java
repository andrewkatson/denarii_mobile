package com.denarii.android.util;

import com.denarii.android.constants.Constants;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordPatternValidator {

  public static boolean isValidPassword(String text) {
    Pattern pattern = Pattern.compile(Constants.PASSWORD_PATTERN);
    Matcher matcher = pattern.matcher(text);
    return matcher.matches();
  }
}
