package com.denarii.android.navigation;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.denarii.android.R;
import com.denarii.android.activities.main.MainActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ComplexNavigationTest {

  /**
   * Use {@link ActivityScenarioRule} to create and launch the activity under test, and close it
   * after test completes. This is a replacement for {@link androidx.test.rule.ActivityTestRule}.
   */
  @Rule
  public ActivityScenarioRule<MainActivity> activityScenarioRule =
      new ActivityScenarioRule<>(MainActivity.class);

  @Rule public TestName testName = new TestName();

  private static String name = "";
  private static String email = "";
  private static String password = "";
  private static String newPassword = "";
  private static String walletName = "";
  private static String walletPassword = "";

  private static String otherName = "";
  private static String otherEmail = "";
  private static String otherPassword = "";
  private static String otherWalletName = "";
  private static String otherWalletPassword = "";

  @Before
  public void beforeTest() {
    name = String.format("%s_name", testName.getMethodName());
    email = String.format("%s_email@email.com", testName.getMethodName());
    password = String.format("%s_password", testName.getMethodName());
    newPassword = String.format("%s_new_password", testName.getMethodName());
    walletName = String.format("%s_wallet_name", testName.getMethodName());
    walletPassword = String.format("%s_wallet_password", testName.getMethodName());

    otherName = String.format("%s_name_other", testName.getMethodName());
    otherEmail = String.format("%s_email_other@email.com", testName.getMethodName());
    otherPassword = String.format("%s_password_other", testName.getMethodName());
    otherWalletName = String.format("%s_wallet_name_other", testName.getMethodName());
    otherWalletPassword = String.format("%s_wallet_password_other", testName.getMethodName());
  }

  @Test
  public void resetPassword_withValidData_resetsPassword() {
    resetPassword(name, email, password, newPassword);

    strictlyCreateWallet(walletName, walletPassword);

    logout();
  }

  @Test
  public void createWallet_withValidData_createsWallet() {
    createWallet(name, email, password, walletName, walletPassword);

    logout();
  }

  @Test
  public void restoreWallet_withValidData_restoresWallet() {
    restoreWallet(name, email, password, walletName, walletPassword);

    logout();
  }

  @Test
  public void openWallet_withValidData_opensWallet() {
    openWallet(name, email, password, walletName, walletPassword);

    logout();
  }

  @Test
  public void sendDenarii_withValidData_sendsDenarii() {
    sendDenarii(
        name,
        email,
        password,
        walletName,
        walletPassword,
        otherName,
        otherEmail,
        otherPassword,
        otherWalletName,
        otherWalletPassword);

    logout();
  }

  @Test
  public void cancelBuyDenarii_withValidData_cancelsBuyDenarii() {
    cancelBuy(
        name,
        email,
        password,
        walletName,
        walletPassword,
        otherName,
        otherEmail,
        otherPassword,
        otherWalletName,
        otherWalletPassword);

    logout();
  }

  @Test
  public void
      createSupportTicketCommentThenResolve_withValidData_createsSupportTicketCommentsThenResolves() {
    commentOnResolveSupportTicket(name, email, password, walletName, walletPassword);

    logout();
  }

  @Test
  public void
      createSupportTicketCommentThenDelete_withValidData_createsSupportTicketCommentsThenDeletes() {
    commentOnDeleteSupportTicket(name, email, password, walletName, walletPassword);

    logout();
  }

  @Test
  public void deleteAccount_withValidData_deletesAccount() {
    deleteAccount(name, email, password, walletName, walletPassword);
  }

  private void resetPassword(String name, String email, String password, String newPassword) {
    registerWithDenarii(name, email, password);

    // Now we are on login
    onView(withId(R.id.login_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.login_forgot_password_button)).perform(click());

    // Now we are on request reset
    onView(withId(R.id.request_reset_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.request_reset_username_or_email_edit_text))
        .perform(typeText(name), closeSoftKeyboard());

    onView(withId(R.id.request_reset_request_reset_button)).perform(click());

    onView(withId(R.id.request_reset_next_button)).check(matches(isDisplayed()));

    onView(withId(R.id.request_reset_next_button)).perform(click());

    // Now we are on verify reset
    onView(withId(R.id.verify_reset_layout)).check(matches(isDisplayed()));

    // A static reset id to make things easier
    onView(withId(R.id.verify_reset_reset_id_edit_text))
        .perform(typeText("123"), closeSoftKeyboard());

    onView(withId(R.id.verify_reset_verify_reset_button)).perform(click());

    onView(withId(R.id.verify_reset_next_button)).check(matches(isDisplayed()));

    onView(withId(R.id.verify_reset_next_button)).perform(click());

    // Now we are on reset password
    onView(withId(R.id.reset_password_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.reset_password_enter_password_edit_text))
        .perform(typeText(newPassword), closeSoftKeyboard());

    onView(withId(R.id.reset_password_confirm_password_edit_text))
        .perform(typeText(newPassword), closeSoftKeyboard());

    onView(withId(R.id.reset_password_reset_password_button)).perform(click());

    onView(withId(R.id.reset_password_next_button)).check(matches(isDisplayed()));

    onView(withId(R.id.reset_password_next_button)).perform(click());

    // Now we are on login
    onView(withId(R.id.login_layout)).check(matches(isDisplayed()));

    strictlyLoginWithDenarii(name, email, newPassword);
  }

  private void createWallet(
      String name, String email, String password, String walletName, String walletPassword) {
    loginWithDenarii(name, email, password);

    strictlyCreateWallet(walletName, walletPassword);
  }

  private void strictlyCreateWallet(String walletName, String walletPassword) {
    onView(withId(R.id.wallet_decision_create_wallet_button)).perform(click());

    // Now we are on create wallet
    onView(withId(R.id.create_wallet_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.create_wallet_enter_name))
        .perform(typeText(walletName), closeSoftKeyboard());

    onView(withId(R.id.create_wallet_enter_password))
        .perform(typeText(walletPassword), closeSoftKeyboard());

    onView(withId(R.id.create_wallet_submit_button)).perform(click());

    onView(withId(R.id.create_wallet_next_button)).check(matches(isDisplayed()));

    onView(withId(R.id.create_wallet_next_button)).perform(click());

    // Now we are on OpenedWallet
    onView(withId(R.id.opened_wallet_layout)).check(matches(isDisplayed()));
  }

  private void restoreWallet(
      String name, String email, String password, String walletName, String walletPassword) {
    createWallet(name, email, password, walletName, walletPassword);

    logout();

    strictlyLoginWithDenarii(name, email, password);

    // Now we are on wallet decision
    onView(withId(R.id.wallet_decision_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.wallet_decision_restore_wallet_button)).perform(click());

    // Now we are on restore wallet
    onView(withId(R.id.restore_wallet_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.restore_wallet_enter_name))
        .perform(typeText(walletName), closeSoftKeyboard());

    onView(withId(R.id.restore_wallet_enter_password))
        .perform(typeText(password), closeSoftKeyboard());

    // A static seed to make testing easier
    onView(withId(R.id.restore_wallet_enter_seed))
        .perform(typeText("some seed here"), closeSoftKeyboard());

    onView(withId(R.id.restore_wallet_submit_button)).perform(click());

    onView(withId(R.id.restore_wallet_next_button)).check(matches(isDisplayed()));

    onView(withId(R.id.restore_wallet_next_button)).perform(click());

    // Now we are on opened wallet
    onView(withId(R.id.opened_wallet_layout)).check(matches(isDisplayed()));
  }

  private void openWallet(
      String name, String email, String password, String walletName, String walletPassword) {
    createWallet(name, email, password, walletName, walletPassword);

    logout();

    strictlyLoginWithDenarii(name, email, password);

    strictlyOpenWallet(walletName, walletPassword);
  }

  private void strictlyOpenWallet(String walletName, String walletPassword) {

    onView(withId(R.id.wallet_decision_open_wallet_button)).perform(click());

    // Now we are on open wallet
    onView(withId(R.id.open_wallet_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.open_wallet_enter_name)).perform(typeText(walletName), closeSoftKeyboard());

    onView(withId(R.id.open_wallet_enter_password))
        .perform(typeText(walletPassword), closeSoftKeyboard());

    onView(withId(R.id.open_wallet_submit_button)).perform(click());

    onView(withId(R.id.open_wallet_next_button)).check(matches(isDisplayed()));

    onView(withId(R.id.open_wallet_next_button)).perform(click());

    // Now we are on opened wallet
    onView(withId(R.id.opened_wallet_layout)).check(matches(isDisplayed()));
  }

  private void cancelBuy(
      String name,
      String email,
      String password,
      String walletName,
      String walletPassword,
      String otherName,
      String otherEmail,
      String otherPassword,
      String otherWalletName,
      String otherWalletPassword) {
    sellDenarii(otherName, otherEmail, otherPassword, otherWalletName, otherWalletPassword);

    logout();

    cancelBuyDenarii(name, email, password, walletName, walletPassword);
  }

  private void sendDenarii(
      String name,
      String email,
      String password,
      String walletName,
      String walletPassword,
      String otherName,
      String otherEmail,
      String otherPassword,
      String otherWalletName,
      String otherWalletPassword) {
    createWallet(otherName, otherEmail, otherPassword, otherWalletName, otherWalletPassword);

    logout();

    createWallet(name, email, password, walletName, walletPassword);

    onView(withId(R.id.opened_wallet_amount_edit_text)).perform(typeText("2"), closeSoftKeyboard());

    // We know this is the address of the first wallet because the first wallet gets 1 statically
    onView(withId(R.id.opened_wallet_amount_edit_text)).perform(typeText("1"), closeSoftKeyboard());

    onView(withId(R.id.opened_wallet_attempt_send_button)).perform(click());

    logout();

    strictlyNavigateToLogin();

    strictlyLoginWithDenarii(otherName, otherEmail, otherPassword);

    strictlyOpenWallet(otherWalletName, otherWalletPassword);

    // Now we are on opened wallet
    onView(withId(R.id.opened_wallet_layout)).check(matches(isDisplayed()));

    // Balance starts at 10 so we can guess itll be 12.
    onView(withId(R.id.opened_wallet_balance_text_view)).check(matches(withText("12")));
  }

  private void commentOnResolveSupportTicket(
      String name, String email, String password, String walletName, String walletPassword) {
    createSupportTicket(name, email, password, walletName, walletPassword);

    commentOnSupportTicket();

    resolveSupportTicket();
  }

  private void commentOnDeleteSupportTicket(
      String name, String email, String password, String walletName, String walletPassword) {
    createSupportTicket(name, email, password, walletName, walletPassword);

    commentOnSupportTicket();

    deleteSupportTicket();
  }

  private void deleteAccount(
      String name, String email, String password, String walletName, String walletPassword) {
    createWallet(name, email, password, walletName, walletPassword);

    onView(withId(R.id.main_menu_layout)).perform(click());

    onView(withId(R.id.settings)).perform(click());

    // On user settings
    onView(withId(R.id.user_settings_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.deleteAccountButton)).perform(click());

    // On login
    onView(withId(R.id.login_layout)).check(matches(isDisplayed()));
  }

  private void logout() {
    onView(withId(R.id.main_menu_layout)).perform(click());

    onView(withId(R.id.settings)).perform(click());

    // On user settings
    onView(withId(R.id.user_settings_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.logoutButton)).perform(click());

    // On login
    onView(withId(R.id.login_layout)).check(matches(isDisplayed()));
  }

  private void navigateToLoginOrRegister() {
    onView(withId(R.id.main_next)).perform(click());

    // Now we are on LoginOrRegister
    onView(withId(R.id.login_or_register_layout)).check(matches(isDisplayed()));
  }

  private void strictlyNavigateToLogin() {
    onView(withId(R.id.login_or_register_login_button)).perform(click());

    // Now we are on Login
    onView(withId(R.id.login_layout)).check(matches(isDisplayed()));
  }

  private void navigateToLogin() {
    navigateToLoginOrRegister();

    strictlyNavigateToLogin();
  }

  private void registerWithDenarii(String name, String email, String password) {
    navigateToLoginOrRegister();

    onView(withId(R.id.login_or_register_register_button)).perform(click());

    // Now we are on register
    onView(withId(R.id.register_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.register_enter_name_edit_text)).perform(typeText(name), closeSoftKeyboard());

    onView(withId(R.id.register_enter_email_edit_text))
        .perform(typeText(email), closeSoftKeyboard());

    onView(withId(R.id.register_enter_password_edit_text))
        .perform(typeText(password), closeSoftKeyboard());

    onView(withId(R.id.register_confirm_password_edit_text))
        .perform(typeText(password), closeSoftKeyboard());

    onView(withId(R.id.register_submit_button)).perform(click());

    onView(withId(R.id.register_next_button)).check(matches(isDisplayed()));

    onView(withId(R.id.register_next_button)).perform(click());

    // Now we are on login
    onView(withId(R.id.login_layout)).check(matches(isDisplayed()));
  }

  private void strictlyLoginWithDenarii(String name, String email, String password) {
    onView(withId(R.id.login_enter_name_edit_text)).perform(typeText(name), closeSoftKeyboard());

    onView(withId(R.id.login_enter_email_edit_text)).perform(typeText(email), closeSoftKeyboard());

    onView(withId(R.id.login_enter_password_edit_text))
        .perform(typeText(password), closeSoftKeyboard());

    onView(withId(R.id.login_submit_button)).perform(click());

    onView(withId(R.id.login_next_button)).check(matches(isDisplayed()));

    onView(withId(R.id.login_next_button)).perform(click());

    // Now we are on wallet decision
    onView(withId(R.id.wallet_decision_layout)).check(matches(isDisplayed()));
  }

  private void loginWithDenarii(String name, String email, String password) {
    registerWithDenarii(name, email, password);

    strictlyLoginWithDenarii(name, email, password);
  }

  private void strictlySetCreditCardInfo() {}

  private void strictlyVerifyIdentity() {}

  private void setupToSellOrBuy(
      String name, String email, String password, String walletName, String walletPassword) {}

  private void sellDenarii(
      String name, String email, String password, String walletName, String walletPassword) {}

  private void buyDenarii(
      String name, String email, String password, String walletName, String walletPassword) {}

  private void cancelBuyDenarii(
      String name, String email, String password, String walletName, String walletPassword) {}

  private void createSupportTicket(
      String name, String email, String password, String walletName, String walletPassword) {}

  private void commentOnSupportTicket() {}

  private void resolveSupportTicket() {}

  private void deleteSupportTicket() {}
}
