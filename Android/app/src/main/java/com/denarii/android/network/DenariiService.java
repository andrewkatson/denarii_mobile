package com.denarii.android.network;

import com.denarii.android.user.DenariiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DenariiService {

    // Returns a single Wallet instance with a user identifier
    @GET("users/{user}/{email}/{password}")
    Call<List<DenariiResponse>> getUserId(@Path("user") String userName, @Path("email") String email, @Path("password") String password);

    // Returns a single Wallet instance with nothing in it
    @GET("users/request_reset/{usernameOrEmail}")
    Call<List<DenariiResponse>> requestPasswordReset(@Path("usernameOrEmail") String usernameOrEmail);

    // Returns a single Wallet instance with nothing in it
    @POST("users/verify_reset/{usernameOrEmail}/{resetId}")
    Call<List<DenariiResponse>> verifyReset(@Path("usernameOrEmail") String usernameOrEmail, @Path("resetId") int resetId);

    // Returns a single Wallet instance with nothing in it
    @PATCH("users/reset_password/{username}/{email}/{password}")
    Call<List<DenariiResponse>> resetPassword(@Path("username") String username, @Path("email") String email, @Path("password") String password);

    // Returns a single Wallet instance with a seed and address
    @POST("users/create/{id}/{wallet}/{password}")
    Call<List<DenariiResponse>> createWallet(@Path("id") int userIdentifier, @Path("wallet") String walletName, @Path("password") String password);

    // Returns a single Wallet instance with address
    @PATCH("users/restore/{id}/{wallet}/{password}/{seed}")
    Call<List<DenariiResponse>> restoreWallet(@Path("id") int userIdentifier, @Path("wallet") String walletName, @Path("password") String password, @Path("seed") String seed);

    // Returns a single Wallet instance with a seed and address
    @GET("users/open/{id}/{wallet}/{password}")
    Call<List<DenariiResponse>> openWallet(@Path("id") int userIdentifier, @Path("wallet") String walletName, @Path("password") String password);

    // Returns a single Wallet instance with the balance of the main address
    @GET("users/balance/{id}/{wallet}")
    Call<List<DenariiResponse>> getBalance(@Path("id") int userIdentifier, @Path("wallet") String walletName);

    // Returns a single Wallet instance with nothing in it
    @POST("users/send/{id}/{wallet}/{address}/{amount}")
    Call<List<DenariiResponse>> sendDenarii(@Path("id") int userIdentifier, @Path("wallet") String walletName, @Path("address") String addressToSendTo, @Path("amount") double amountToSend);

}
