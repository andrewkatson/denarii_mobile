package com.denarii.android.navigation;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
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

  private void resetPassword(String name, String email, String password, String newPassword) {}

  private void createWallet(
      String name, String email, String password, String walletName, String walletPassword) {}

  private void strictlyCreateWallet(String walletName, String walletPassword) {}

  private void restoreWallet(
      String name, String email, String password, String walletName, String walletPassword) {}

  private void openWallet(
      String name, String email, String password, String walletName, String walletPassword) {}

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
      String otherWalletPassword) {}

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
      String otherWalletPassword) {}

  private void commentOnResolveSupportTicket(
      String name, String email, String password, String walletName, String walletPassword) {}

  private void commentOnDeleteSupportTicket(
      String name, String email, String password, String walletName, String walletPassword) {}

  private void deleteAccount(
      String name, String email, String password, String walletName, String walletPassword) {}

  private void logout() {}
}
