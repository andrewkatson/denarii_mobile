package com.denarii.android.network;

import com.denarii.android.user.Wallet;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DenariiService {

    @GET("users/{user}/{email}")
    Call<Wallet> getUserId(@Path("user") String userName, @Path("email") String email);

    @POST("users/{id}/{wallet}/{password}/create")
    Call<Wallet> createWallet(@Path("id") String userId, @Path("wallet") String walletName, @Path("password") String password);

    @PATCH("users/{id}/{wallet}/{password}/{seed}/restore")
    Call<Wallet> restoreWallet(@Path("id") String userId, @Path("wallet") String walletName, @Path("password") String password, @Path("seed") String seed);

    @GET("users/{id}/{wallet}/{password}/open")
    Call<Wallet> openWallet(@Path("id") String userId, @Path("wallet") String walletName, @Path("password") String password);

    @GET("users/{id}/{wallet}/balance")
    Call<Wallet> getBalance(@Path("id") String userId, @Path("wallet") String walletName);

    @POST("users/{id}/{wallet}/{address}/{amount}/send")
    Call<Wallet> sendDenarii(@Path("id") String userId, @Path("wallet") String walletName, @Path("address") String addressToSendTo, @Path("amount") double amountToSend);
}
