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
    @GET("users/request_reset/{username_or_email}")
    Call<List<DenariiResponse>> requestPasswordReset(@Path("username_or_email") String usernameOrEmail);

    // Returns a single Wallet instance with nothing in it
    @POST("users/verify_reset/{username_or_email}/{reset_id}")
    Call<List<DenariiResponse>> verifyReset(@Path("username_or_email") String usernameOrEmail, @Path("reset_id") int resetId);

    // Returns a single Wallet instance with nothing in it
    @PATCH("users/reset_password/{username}/{email}/{password}")
    Call<List<DenariiResponse>> resetPassword(@Path("username") String username, @Path("email") String email, @Path("password") String password);

    // Returns a single Wallet instance with a seed and address
    @POST("users/create/{user_id}/{wallet_name}/{password}")
    Call<List<DenariiResponse>> createWallet(@Path("user_id") String userIdentifier, @Path("wallet_name") String walletName, @Path("password") String password);

    // Returns a single Wallet instance with address
    @PATCH("users/restore/{user_id}/{wallet_name}/{password}/{seed}")
    Call<List<DenariiResponse>> restoreWallet(@Path("user_id") String userIdentifier, @Path("wallet_name") String walletName, @Path("password") String password, @Path("seed") String seed);

    // Returns a single Wallet instance with a seed and address
    @GET("users/open/{user_id}/{wallet_name}/{password}")
    Call<List<DenariiResponse>> openWallet(@Path("user_id") String userIdentifier, @Path("wallet_name") String walletName, @Path("password") String password);

    // Returns a single Wallet instance with the balance of the main address
    @GET("users/balance/{user_id}/{wallet_name}")
    Call<List<DenariiResponse>> getBalance(@Path("user_id") String userIdentifier, @Path("wallet_name") String walletName);

    // Returns a single Wallet instance with nothing in it
    @POST("users/send/{user_id}/{wallet_name}/{address}/{amount}")
    Call<List<DenariiResponse>> sendDenarii(@Path("user_id") String userIdentifier, @Path("wallet_name") String walletName, @Path("address") String addressToSendTo, @Path("amount") double amountToSend);

}
