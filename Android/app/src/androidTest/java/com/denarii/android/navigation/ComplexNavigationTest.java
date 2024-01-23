package com.denarii.android.navigation;

import static androidx.test.core.graphics.BitmapStorage.writeToTestStorage;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.repeatedlyUntil;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.screenshot.ViewInteractionCapture.captureToBitmap;
import static com.adevinta.android.barista.interaction.BaristaMenuClickInteractions.clickMenu;
import static com.denarii.android.testing.MatcherDenarii.hasValueEqualTo;
import static com.denarii.android.testing.ViewActionDenarii.hardSwipeDown;
import static com.denarii.android.testing.ViewActionDenarii.withCustomConstraints;
import static org.hamcrest.CoreMatchers.allOf;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.denarii.android.R;
import com.denarii.android.activities.main.MainActivity;
import java.io.IOException;
import java.util.function.Function;
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
    password = String.format("%s_password1#", testName.getMethodName());
    newPassword = String.format("%s_new_password1&", testName.getMethodName());
    walletName = String.format("%s_wallet_name", testName.getMethodName());
    walletPassword = String.format("%s_wallet_password9$", testName.getMethodName());

    otherName = String.format("%s_name_other", testName.getMethodName());
    otherEmail = String.format("%s_email_other@email.com", testName.getMethodName());
    otherPassword = String.format("%s_password_other7#", testName.getMethodName());
    otherWalletName = String.format("%s_wallet_name_other", testName.getMethodName());
    otherWalletPassword = String.format("%s_wallet_password_other2#", testName.getMethodName());
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
  public void cancelSellDenarii_withValidData_cancelsSellDenarii() throws IOException {
    cancelSellDenarii(name, email, password, walletName, walletPassword);

    logout();
  }

  @Test
  public void cancelBuyDenarii_withValidData_cancelsBuyDenarii() throws IOException {
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
      createSupportTicketCommentThenResolve_withValidData_createsSupportTicketCommentsThenResolves()
          throws IOException {
    commentOnResolveSupportTicket(name, email, password, walletName, walletPassword);

    logout();
  }

  @Test
  public void
      createSupportTicketCommentThenDelete_withValidData_createsSupportTicketCommentsThenDeletes()
          throws IOException {
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

    onView(withId(R.id.rs_enter_name_edit_text)).perform(typeText(name), closeSoftKeyboard());

    onView(withId(R.id.rs_enter_email_edit_text)).perform(typeText(email), closeSoftKeyboard());

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

  private String createWallet(
      String name, String email, String password, String walletName, String walletPassword) {
    return createWallet(name, email, password, walletName, walletPassword, false);
  }

  private String createWallet(
      String name,
      String email,
      String password,
      String walletName,
      String walletPassword,
      boolean getAddress) {
    loginWithDenarii(name, email, password);

    return strictlyCreateWallet(walletName, walletPassword, getAddress);
  }

  private String strictlyCreateWallet(String walletName, String walletPassword) {
    return strictlyCreateWallet(walletName, walletPassword, false);
  }

  private String strictlyCreateWallet(
      String walletName, String walletPassword, boolean getAddress) {
    onView(withId(R.id.wallet_decision_create_wallet_button)).perform(click());

    // Now we are on create wallet
    onView(withId(R.id.create_wallet_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.create_wallet_enter_name))
        .perform(typeText(walletName), closeSoftKeyboard());

    onView(withId(R.id.create_wallet_enter_password))
        .perform(typeText(walletPassword), closeSoftKeyboard());

    onView(withId(R.id.create_wallet_confirm_password))
        .perform(typeText(walletPassword), closeSoftKeyboard());

    onView(withId(R.id.create_wallet_submit_button)).perform(click());

    onView(withId(R.id.create_wallet_next_button)).check(matches(isDisplayed()));

    onView(withId(R.id.create_wallet_next_button)).perform(click());

    // Now we are on OpenedWallet
    onView(withId(R.id.opened_wallet_layout)).check(matches(isDisplayed()));

    if (!getAddress) {
      return "-1";
    }

    long address = 1000000000L;
    Function<Long, String> getAddressString =
        (addressLong) -> String.format("Own Address: %s", addressLong);
    boolean addressNotFound = true;

    while (addressNotFound) {
      try {
        onView(withId(R.id.opened_wallet_address_text_view))
            .check(matches(hasValueEqualTo(getAddressString.apply(address))));
        addressNotFound = false;
      } catch (Exception unused) {
        address += 1;
      }
    }

    return String.valueOf(address);
  }

  private void restoreWallet(
      String name, String email, String password, String walletName, String walletPassword) {
    createWallet(name, email, password, walletName, walletPassword);

    logout();

    strictlyNavigateToLogin();

    strictlyLoginWithDenarii(name, email, password);

    // Now we are on wallet decision
    onView(withId(R.id.wallet_decision_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.wallet_decision_restore_wallet_button)).perform(click());

    // Now we are on restore wallet
    onView(withId(R.id.restore_wallet_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.restore_wallet_enter_name))
        .perform(typeText(walletName), closeSoftKeyboard());

    onView(withId(R.id.restore_wallet_enter_password))
        .perform(typeText(walletPassword), closeSoftKeyboard());

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

    strictlyNavigateToLogin();

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
      String otherWalletPassword)
      throws IOException {
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
    // In this one case we need the address. Normally it slows things down so we dont get it.
    String sendToAddress =
        createWallet(
            otherName, otherEmail, otherPassword, otherWalletName, otherWalletPassword, true);

    logout();

    createWallet(name, email, password, walletName, walletPassword);

    onView(withId(R.id.opened_wallet_amount_edit_text)).perform(typeText("2"), closeSoftKeyboard());

    onView(withId(R.id.opened_wallet_to_edit_text))
        .perform(typeText(sendToAddress), closeSoftKeyboard());

    onView(withId(R.id.opened_wallet_attempt_send_button)).perform(click());

    logout();

    strictlyNavigateToLogin();

    strictlyLoginWithDenarii(otherName, otherEmail, otherPassword);

    strictlyOpenWallet(otherWalletName, otherWalletPassword);

    // Now we are on opened wallet
    onView(withId(R.id.opened_wallet_layout)).check(matches(isDisplayed()));

    // Balance starts at 10 so we can guess itll be 12.
    onView(withId(R.id.opened_wallet_balance_text_view))
        .check(matches(withText("Balance: 12.000000")));
  }

  private void commentOnResolveSupportTicket(
      String name, String email, String password, String walletName, String walletPassword)
      throws IOException {
    createSupportTicket(name, email, password, walletName, walletPassword);

    commentOnSupportTicket();

    resolveSupportTicket();
  }

  private void commentOnDeleteSupportTicket(
      String name, String email, String password, String walletName, String walletPassword)
      throws IOException {
    createSupportTicket(name, email, password, walletName, walletPassword);

    commentOnSupportTicket();

    deleteSupportTicket();
  }

  private void deleteAccount(
      String name, String email, String password, String walletName, String walletPassword) {
    createWallet(name, email, password, walletName, walletPassword);

    clickMenu(R.id.settings);

    // On user settings
    onView(withId(R.id.user_settings_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.deleteAccountButton)).perform(click());

    // On login or register
    onView(withId(R.id.login_or_register_layout)).check(matches(isDisplayed()));
  }

  private void logout() {
    clickMenu(R.id.settings);

    // On user settings
    onView(withId(R.id.user_settings_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.logoutButton)).perform(click());

    // On login or register
    onView(withId(R.id.login_or_register_layout)).check(matches(isDisplayed()));
  }

  private void navigateToLoginOrRegister() {
    // Some tests start on login or register so check for that first
    try {
      onView(withId(R.id.main_layout)).check(matches(isDisplayed()));

      onView(withId(R.id.main_next)).perform(click());
    } catch (NoMatchingViewException ignored) {

    } finally {
      // Now we are on LoginOrRegister
      onView(withId(R.id.login_or_register_layout)).check(matches(isDisplayed()));
    }
  }

  private void strictlyNavigateToLogin() {
    onView(withId(R.id.login_or_register_login_button)).perform(click());

    // Now we are on Login
    onView(withId(R.id.login_layout)).check(matches(isDisplayed()));
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

  private void strictlySetCreditCardInfo() {
    onView(withId(R.id.creditCardInfoNumber)).perform(replaceText("123"), closeSoftKeyboard());

    onView(withId(R.id.creditCardInfoExpiration))
        .perform(replaceText("01/12"), closeSoftKeyboard());

    onView(withId(R.id.creditCardInfoSecurityCode))
        .perform(replaceText("001"), closeSoftKeyboard());

    onView(withId(R.id.creditCardInfoSubmit)).perform(click());
  }

  private void strictlyVerifyIdentity(String email) {
    onView(withId(R.id.firstName)).perform(replaceText("andrew"), closeSoftKeyboard());

    onView(withId(R.id.middleInitial)).perform(replaceText("v"), closeSoftKeyboard());

    onView(withId(R.id.lastName)).perform(replaceText("poppy"), closeSoftKeyboard());

    onView(withId(R.id.email)).perform(replaceText(email), closeSoftKeyboard());

    onView(withId(R.id.dateOfBirth)).perform(replaceText("01/22/1991"), closeSoftKeyboard());

    onView(withId(R.id.socialSecurityNumber)).perform(replaceText("11111111"), closeSoftKeyboard());

    onView(withId(R.id.zipCode)).perform(replaceText("33332232"), closeSoftKeyboard());

    onView(withId(R.id.verificationPhoneNumber))
        .perform(replaceText("1234567890"), closeSoftKeyboard());

    onView(withId(R.id.workCity)).perform(replaceText("San Jose"), closeSoftKeyboard());

    onView(withId(R.id.workState)).perform(replaceText("California"), closeSoftKeyboard());

    onView(withId(R.id.workCountry)).perform(replaceText("United States"), closeSoftKeyboard());

    onView(withId(R.id.verificationSubmit)).perform(click());
  }

  private void setupToSellOrBuy(
      String name, String email, String password, String walletName, String walletPassword) {
    createWallet(name, email, password, walletName, walletPassword);

    clickMenu(R.id.credit_card_info);

    // On credit card info
    onView(withId(R.id.credit_card_info_layout)).check(matches(isDisplayed()));

    strictlySetCreditCardInfo();

    clickMenu(R.id.verification);

    // On verification
    onView(withId(R.id.verification_layout)).check(matches(isDisplayed()));

    strictlyVerifyIdentity(email);
  }

  private void sellDenarii(
      String name, String email, String password, String walletName, String walletPassword)
      throws IOException {
    setupToSellOrBuy(name, email, password, walletName, walletPassword);

    clickMenu(R.id.sell_denarii);

    // On sell denarii
    onView(withId(R.id.sell_denarii_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.sellAmount)).perform(replaceText("2"), closeSoftKeyboard());

    onView(withId(R.id.sellPrice)).perform(replaceText("100"), closeSoftKeyboard());

    onView(withId(R.id.submitSell)).perform(click());

    // Refresh
    onView(withId(R.id.sell_denarii_refresh_layout))
        .perform(repeatedlyUntil(hardSwipeDown(), hasDescendant(withText("Cancel")), 10));

    // Take a screenshot
    writeToTestStorage(
        captureToBitmap(onView(withId(R.id.sell_denarii_layout))),
        getClass().getSimpleName() + "_" + testName.getMethodName());
  }

  private void buyDenarii(
      String name, String email, String password, String walletName, String walletPassword)
      throws IOException {
    setupToSellOrBuy(name, email, password, walletName, walletPassword);

    clickMenu(R.id.buy_denarii);

    // On buy denarii
    onView(withId(R.id.buy_denarii_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.buy_denarii_amount)).perform(replaceText("1"), closeSoftKeyboard());

    onView(withId(R.id.buy_denarii_price)).perform(replaceText("110"), closeSoftKeyboard());

    onView(withId(R.id.buy_denarii_submit)).perform(click());

    // Refresh
    onView(withId(R.id.buy_denarii_refresh_layout))
        .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));

    // Take a screenshot
    writeToTestStorage(
        captureToBitmap(onView(withId(R.id.buy_denarii_layout))),
        getClass().getSimpleName() + "_" + testName.getMethodName());
  }

  private void cancelSellDenarii(
      String name, String email, String password, String walletName, String walletPassword)
      throws IOException {
    sellDenarii(name, email, password, walletName, walletPassword);

    onView(allOf(withId(R.id.ownAsksCancelBuyItem), withText("Cancel"))).perform(click());

    // Refresh
    onView(withId(R.id.sell_denarii_refresh_layout))
        .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));

    // Take a screenshot
    writeToTestStorage(
        captureToBitmap(onView(withId(R.id.sell_denarii_layout))),
        getClass().getSimpleName() + "_" + testName.getMethodName());
  }

  private void cancelBuyDenarii(
      String name, String email, String password, String walletName, String walletPassword)
      throws IOException {
    buyDenarii(name, email, password, walletName, walletPassword);

    onView(allOf(withId(R.id.buyCancelBuyItem), withText("Cancel"))).perform(click());

    // Refresh
    onView(withId(R.id.buy_denarii_refresh_layout))
        .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));

    // Take a screenshot
    writeToTestStorage(
        captureToBitmap(onView(withId(R.id.buy_denarii_layout))),
        getClass().getSimpleName() + "_" + testName.getMethodName());
  }

  private void createSupportTicket(
      String name, String email, String password, String walletName, String walletPassword) {
    createWallet(name, email, password, walletName, walletPassword);

    clickMenu(R.id.settings);

    // On user settings
    onView(withId(R.id.user_settings_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.supportTicketsButton)).perform(click());

    // On support tickets
    onView(withId(R.id.support_tickets_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.createSupportTicket)).perform(click());

    // On create support ticket
    onView(withId(R.id.create_support_ticket_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.supportTicketTitle)).perform(replaceText("title"), closeSoftKeyboard());

    onView(withId(R.id.supportTicketDescription))
        .perform(replaceText("description"), closeSoftKeyboard());

    onView(withId(R.id.createSupportTicketSubmit)).perform(click());

    // On support ticket details
    onView(withId(R.id.support_ticket_details_layout)).check(matches(isDisplayed()));
  }

  private void commentOnSupportTicket() throws IOException {
    onView(withId(R.id.supportTicketDetailsCommentBox))
        .perform(replaceText("comment"), closeSoftKeyboard());

    onView(withId(R.id.commentBoxSubmit)).perform(click());

    // Refresh
    onView(withId(R.id.support_ticket_details_refresh_layout))
        .perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));

    // Take a screenshot
    writeToTestStorage(
        captureToBitmap(onView(withId(R.id.support_ticket_details_layout))),
        getClass().getSimpleName() + "_" + testName.getMethodName());
  }

  private void resolveSupportTicket() {
    onView(withId(R.id.resolveSupportTicket)).perform(click());

    // On support tickets
    onView(withId(R.id.support_tickets_layout)).check(matches(isDisplayed()));
  }

  private void deleteSupportTicket() {
    onView(withId(R.id.deleteSupportTicket)).perform(click());

    // On support tickets
    onView(withId(R.id.support_tickets_layout)).check(matches(isDisplayed()));
  }
}
