package com.denarii.android.network;

import com.denarii.android.user.DenariiResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DenariiService {

  // Returns a single Wallet instance with a user identifier
  @GET("users/{user}/{email}/{password}")
  Call<List<DenariiResponse>> getUserId(
      @Path("user") String userName,
      @Path("email") String email,
      @Path("password") String password);

  // Returns a single Wallet instance with nothing in it
  @GET("users/request_reset/{username_or_email}")
  Call<List<DenariiResponse>> requestPasswordReset(
      @Path("username_or_email") String usernameOrEmail);

  // Returns a single Wallet instance with nothing in it
  @PATCH("users/verify_reset/{username_or_email}/{reset_id}")
  Call<List<DenariiResponse>> verifyReset(
      @Path("username_or_email") String usernameOrEmail, @Path("reset_id") int resetId);

  // Returns a single Wallet instance with nothing in it
  @PATCH("users/reset_password/{username}/{email}/{password}")
  Call<List<DenariiResponse>> resetPassword(
      @Path("username") String username,
      @Path("email") String email,
      @Path("password") String password);

  // Returns a single Wallet instance with a seed and address
  @PATCH("users/create/{user_id}/{wallet_name}/{password}")
  Call<List<DenariiResponse>> createWallet(
      @Path("user_id") String userIdentifier,
      @Path("wallet_name") String walletName,
      @Path("password") String password);

  // Returns a single Wallet instance with address
  @PATCH("users/restore/{user_id}/{wallet_name}/{password}/{seed}")
  Call<List<DenariiResponse>> restoreWallet(
      @Path("user_id") String userIdentifier,
      @Path("wallet_name") String walletName,
      @Path("password") String password,
      @Path("seed") String seed);

  // Returns a single Wallet instance with a seed and address
  @GET("users/open/{user_id}/{wallet_name}/{password}")
  Call<List<DenariiResponse>> openWallet(
      @Path("user_id") String userIdentifier,
      @Path("wallet_name") String walletName,
      @Path("password") String password);

  // Returns a single Wallet instance with the balance of the main address
  @GET("users/balance/{user_id}/{wallet_name}")
  Call<List<DenariiResponse>> getBalance(
      @Path("user_id") String userIdentifier, @Path("wallet_name") String walletName);

  // Returns a single Wallet instance with nothing in it
  @PATCH("users/send/{user_id}/{wallet_name}/{address}/{amount}")
  Call<List<DenariiResponse>> sendDenarii(
      @Path("user_id") String userIdentifier,
      @Path("wallet_name") String walletName,
      @Path("address") String addressToSendTo,
      @Path("amount") double amountToSend);

  // Returns a list of responses with ask_id, asking_price, and amount
  @GET("users/get_prices/{user_id}")
  Call<List<DenariiResponse>> getPrices(@Path("userId") String userIdentifier);

  // Returns a list of responses with ask_id
  @PATCH(
      "users/buy_denarii/{user_id}/{amount}/{bid_price}/{buy_regardless_of_price}/{fail_if_full_amount_isnt_met}")
  Call<List<DenariiResponse>> buyDenarii(
      @Path("user_id") String userIdentifier,
      @Path("amount") String amount,
      @Path("bid_price") String bidPrice,
      @Path("buy_regardless_of_price") String buyRegardlessOfPrice,
      @Path("fail_if_full_amount_isnt_met") String failIfFullAmountIsntMet);

  // Returns a single response with ask_id and amount_bought
  @PATCH("users/transfer_denarii/{user_id}/{ask_id}")
  Call<List<DenariiResponse>> transferDenarii(
      @Path("user_id") String userIdentifier, @Path("ask_id") String askIdentifier);

  // Returns a single response with ask_id, amount, and asking_price
  @PATCH("users/make_denarii_ask/{user_id}/{amount}/{asking_price}")
  Call<List<DenariiResponse>> makeDenariiAsk(
      @Path("user_id") String userIdentifier,
      @Path("amount") String amount,
      @Path("asking_price") String askingPrice);

  // Returns a list of responses with ask_id, amount, asking_price, and amount_bought
  @GET("users/poll_for_compeleted_transaction/{user_id}")
  Call<List<DenariiResponse>> pollForCompletedTransaction(@Path("user_id") String userIdentifier);

  // Returns a single response with ask_id
  @DELETE("users/cancel_ask/{user_id}/{ask_id}")
  Call<List<DenariiResponse>> cancelAsk(
      @Path("user_id") String userIdentifier, @Path("ask_id") String askIdentifier);

  // Returns a single response with has_credit_card_info
  @GET("users/has_credit_card_info/{user_id}")
  Call<List<DenariiResponse>> hasCreditCardInfo(@Path("user_id") String userIdentifier);

  // Returns a single response with no fields in it
  @PATCH(
      "users/set_credit_card_info/{user_id}/{card_number}/{expiration_date_month}/{expiration_date_year}/{security_code}")
  Call<List<DenariiResponse>> setCreditCardInfo(
      @Path("user_id") String userIdentifier,
      @Path("card_number") String cardNumber,
      @Path("expiration_date_month") String expirationDateMonth,
      @Path("expirationDateYear") String expirationDateYear,
      @Path("security_code") String securityCode);

  // Returns a single response with no fields in it
  @DELETE("users/clear_credit_card_info/{user_id}")
  Call<List<DenariiResponse>> clearCreditCardInfo(@Path("user_id") String userIdentifier);

  // Returns a single response with no fields in it
  @PATCH("users/get_money_from_buyer/{user_id}/{amount}/{currency}")
  Call<List<DenariiResponse>> getMoneyFromBuyer(
      @Path("user_id") String userIdentifier,
      @Path("amount") String amount,
      @Path("currency") String currency);

  // Returns a single response with no fields in it
  @PATCH("users/send_money_to_seller/{user_id}/{amount}/{currency}")
  Call<List<DenariiResponse>> sendMoneyToSeller(
      @Path("user_id") String userIdentifier,
      @Path("amount") String amount,
      @Path("currency") String currency);

  // Returns a single response with ask_id and transaction_was_settled
  @PATCH("users/is_transaction_settled/{user_id}/{ask_id}")
  Call<List<DenariiResponse>> isTransactionSettled(
      @Path("user_id") String userIdentifier, @Path("ask_id") String askIdentifier);

  // Returns a single response with no fields
  @DELETE("users/delete_user/{user_id}")
  Call<List<DenariiResponse>> deleteUser(@Path("user_id") String userIdentifier);

  // Returns a single response with ask_id, amount, and amount_bought
  @GET("users/get_ask_with_identifier/{user_id}/{ask_id}")
  Call<List<DenariiResponse>> getAskWithIdentifier(
      @Path("user_id") String userIdentifier, @Path("ask_id") String askIdentifier);

  // Returns a single response with ask_id
  @PATCH("users/transfer_denarii_back_to_seller/{user_id}/{ask_id}")
  Call<List<DenariiResponse>> transferDenariiBackToSeller(
      @Path("user_id") String userIdentifier, @Path("ask_id") String askIdentifier);

  // Returns a single response with no fields
  @PATCH("users/send_money_back_to_buyer/{user_id}/{amount}/{currency}")
  Call<List<DenariiResponse>> sendMoneyBackToBuyer(
      @Path("user_id") String userIdentifier,
      @Path("amount") String amount,
      @Path("currency") String currency);

  // Returns a single response with no fields
  @DELETE("users/cancel_buy_of_ask/{user_id}/{ask_id}")
  Call<List<DenariiResponse>> cancelBuyOfAsk(
      @Path("user_id") String userIdentifier, @Path("ask_id") String askIdentifier);

  // Returns single response with verification_status
  @PATCH(
      "users/verify_identity/{user_id}/{first_name}/{middle_name}/{last_name}/{email}/{dob}/{ssn}/{zipcode}/{phone}/{work_locations}")
  Call<List<DenariiResponse>> verifyIdentity(
      @Path("user_id") String userIdentifier,
      @Path("first_name") String firstName,
      @Path("middle_name") String middleName,
      @Path("last_name") String lastName,
      @Path("email") String email,
      @Path("dob") String dateOfBirth,
      @Path("ssn") String socialSecurityNumber,
      @Path("zipcode") String zipcode,
      @Path("phone") String phone,
      @Path("work_locations") String workLocations);

  // Returns a single response with verification_status
  @GET("users/is_a_verified_person/{user_id}")
  Call<List<DenariiResponse>> isAVerifiedPerson(@Path("user_id") String userIdentifier);

  // Returns a list of responses with ask_id, amount, asking_price, amount_bought
  @GET("users/get_all_asks/{user_id}")
  Call<List<DenariiResponse>> getAllAsks(@Path("user_id") String userIdentifier);

  // Returns a list of responses with ask_id, amount, asking_price, amount_bought
  @GET("users/get_all_buys/{user_id}")
  Call<List<DenariiResponse>> getAllBuys(@Path("user_id") String userIdentifier);

  // Returns a single response with support_ticket_id and creation_time_body
  @POST("users/create_support_ticket/{user_id}/{title}/{description}")
  Call<List<DenariiResponse>> createSupportTicket(
      @Path("user_id") String userIdentifier,
      @Path("title") String title,
      @Path("description") String description);

  // Returns a single response with support_ticket_id, updated_time_body, "comment_id"
  @PATCH("users/update_support_ticket/{user_id}/{support_ticket_id}/{comment}")
  Call<List<DenariiResponse>> updateSupportTicket(
      @Path("user_id") String userIdentifier,
      @Path("support_ticket_id") String supportTicketIdentifier,
      @Path("comment") String comment);

  // Returns a single response with support_ticket_id in it
  @DELETE("users/delete_support_ticket/{user_id}/{support_ticket_id}")
  Call<List<DenariiResponse>> deleteSupportTicket(
      @Path("user_id") String userIdentifier,
      @Path("support_ticket_id") String supportTicketIdentifier);

  // Returns a list of responses with support_ticket_id, author, title, description,
  // updated_time_body, creation_time_body, is_resolved
  @GET("users/get_support_tickets/{user_id}/{can_be_resolved}")
  Call<List<DenariiResponse>> getSupportTickets(
      @Path("user_id") String userIdentifier, @Path("can_be_resolved") String canBeResolved);

  // Returns a single response with support_ticket_id, author, title, description,
  // updated_time_body, creation_time_body, is_resolved
  @GET("users/get_support_tickets/{user_id}/{support_ticket_id}")
  Call<List<DenariiResponse>> getSupportTicket(
      @Path("user_id") String userIdentifier,
      @Path("support_ticket_id") String supportTicketIdentifier);

  // Returns a list of responses with author, content, updated_time_body, creation_time_body
  @GET("users/get_comments_on_ticket/{user_id}/{support_ticket_id}")
  Call<List<DenariiResponse>> getCommentsOnTicket(
      @Path("user_id") String userIdentifier,
      @Path("support_ticket_id") String supportTicketIdentifier);

  // Returns a single response with support_ticket_id and updated_time_body
  @PATCH("users/resolve_support_ticket/{user_id}/{support_ticket_id}")
  Call<List<DenariiResponse>> resolveSupportTicket(
      @Path("user_id") String userIdentifier,
      @Path("support_ticket_id") String supportTicketIdentifier);

  // Returns a list of responses with ask_id, amount, asking_price, and amount_bought
  @GET("users/poll_for_escrowed_transaction/{user_id}")
  Call<List<DenariiResponse>> pollForEscrowedTransaction(@Path("user_id") String userIdentifier);

  // Returns a single response with no fields
  @PATCH("users/logout_user/{user_id}")
  Call<List<DenariiResponse>> logoutUser(@Path("user_id") String userIdentifier);
}
