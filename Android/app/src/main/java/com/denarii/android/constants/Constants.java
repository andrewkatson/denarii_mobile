package com.denarii.android.constants;

public class Constants {
  public static final String USER_DETAILS = "user_details";
  public static final String BASE_URL = "https://denariimobilebackend.com";
  public static final String RESET_PASSWORD_USERNAME_OR_EMAIL = "password_resetter";
  public static boolean DEBUG = false;

  public static final String PASSWORD_PATTERN =
      "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

  public static final String DOUBLE_PATTERN = "^\\d{1,100}[.,]{0,1}\\d{0,100}$";

  public static final String PARAGRAPH_OF_CHARS_PATTERN = "^[\\w \\n]{5,3000}$";

  public static final String ALPHANUMERIC_PATTERN = "^\\w{10,500}$";

  public static final String SINGLE_LETTER_PATTERN = "^\\w{1}$";

  public static final String NAME_PATTERN = "^[a-zA-Z]{3,100}$";

  public static final String DIGITS_ONLY = "^\\d{1,100}$";

  public static final String SLASH_DATE_PATTERN = "^[\\d+/]{3,100}$";

  public static final String SEED_PATTERN = "^[\\w ]{10,1000}$";

  public static final String DIGITS_AND_DASHES_PATTERN = "^[\\d-]{3,100}$";

  public static final String PHONE_NUMBER_PATTERN =
      "^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";

  public static final String ALPHANUMERIC_PATTERN_WITH_SPACES = "^[\\w ]{5,100}$";
}
