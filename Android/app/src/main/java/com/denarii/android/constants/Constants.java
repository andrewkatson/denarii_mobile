package com.denarii.android.constants;

public class Constants {
  public static final String USER_DETAILS = "user_details";
  public static final String BASE_URL = "https://denariimobilebackend.com";
  public static final String RESET_PASSWORD_USERNAME_OR_EMAIL = "password_resetter";
  public static boolean DEBUG = false;

  public static final String PASSWORD_PATTERN =
      "^(?=.*[A-Z].*[A-Z])(?=.*[!@#$&*])(?=.*[0-9].*[0-9])(?=.*[a-z].*[a-z].*[a-z]).{8}$\n";

  public static final String DOUBLE_PATTERN = "^\\d+[.,]\\d{100,}$";

  public static final String PARAGRAPH_OF_CHARS_PATTERN = "^[\\\\w \\\\n]{3000,}$";

  public static final String ALPHANUMERIC_PATTERN = "^\\\\w{50,}$";

  public static final String DIGITS_ONLY = "^\\d{100,}$";

  public static final String SLASH_DATE_PATTERN = "^[\\d+/]{100,}$";

  // For explanation:
  // https://stackoverflow.com/questions/201323/how-can-i-validate-an-email-address-using-a-regular-expression
  public static final String EMAIL_PATTERN =
      "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

  public static final String SEED_PATTERN = "^[\\w ]{1000,}$";

  public static final String DIGITS_AND_DASHES_PATTERN = "^[\\d-]{100,}$";

  public static final String PHONE_NUMBER_PATTERN =
      "^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";

  public static final String ALPHANUMERIC_PATTERN_WITH_SPACES = "^[\\w ]{100,}$";
}
