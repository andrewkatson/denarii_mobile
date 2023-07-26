package com.denarii.android.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DenariiService {

    // Returns a single Wallet instance with a user identifier
    @GET("users/{user}/{email}/{password}")
    Call<List<Wallet>> getUserId(@Path("user") String userName, @Path("email") String email, @Path("password") String password);

    // Returns a single Wallet instance with nothing in it
    @GET("users/{usernameOrEmail}/request_reset")
    Call<List<Wallet>> requestPasswordReset(@Path("usernameOrEmail") String usernameOrEmail);

    // Returns a single Wallet instance with nothing in it
    @POST("users/transfer/{usernameOrEmail}/{resetId}/verify_reset")
    Call<List<Wallet>> verifyReset(@Path("usernameOrEmail") String usernameOrEmail, @Path("resetId") int resetId);

    // Returns a single Wallet instance with nothing in it
    @PATCH("users/transfer/{username}/{email}/{password}/reset_password")
    Call<List<Wallet>> resetPassword(@Path("username") String username, @Path("email") String email, @Path("password") String password);

    // Returns a single Wallet instance with a seed and address
    @POST("users/transfer/{id}/{wallet}/{password}/create")
    Call<List<Wallet>> createWallet(@Path("id") int userIdentifier, @Path("wallet") String walletName, @Path("password") String password);

    // Returns a single Wallet instance with address
    @PATCH("users/transfer/{id}/{wallet}/{password}/{seed}/restore")
    Call<List<Wallet>> restoreWallet(@Path("id") int userIdentifier, @Path("wallet") String walletName, @Path("password") String password, @Path("seed") String seed);

    // Returns a single Wallet instance with a seed and address
    @GET("users/transfer/{id}/{wallet}/{password}/open")
    Call<List<Wallet>> openWallet(@Path("id") int userIdentifier, @Path("wallet") String walletName, @Path("password") String password);

    // Returns a single Wallet instance with the balance of the main address
    @GET("users/transfer/{id}/{wallet}/balance")
    Call<List<Wallet>> getBalance(@Path("id") int userIdentifier, @Path("wallet") String walletName);

    // Returns a single Wallet instance with nothing in it
    @POST("users/transfer/{id}/{wallet}/{address}/{amount}/send")
    Call<List<Wallet>> sendDenarii(@Path("id") int userIdentifier, @Path("wallet") String walletName, @Path("address") String addressToSendTo, @Path("amount") double amountToSend);

    // Initializes the Denarii Service version to use
    DenariiService getInstance();
}
