package com.denarii.android;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DenariiService {

    @POST("users/{user}/create")
    Call<Wallet> createWallet(@Path("user") String userName);

    @PATCH("users/{user}/{wallet}/{password}/{seed}/restore")
    Call<Wallet> restoreWallet(@Path("user") String userName, @Path("wallet") String walletName, @Path("password") String password, @Path("seed") String seed);

    @GET("users/{user}/{wallet}/{password}/open")
    Call<Wallet> openWallet(@Path("user") String userName, @Path("wallet") String walletName, @Path("password") String password);

    @GET("users/{user}/{wallet}/balance")
    Call<Wallet> getBalance(@Path("user") String userName, @Path("wallet") String walletName);

    @GET("users/{user}/{wallet}/{amount}/send")
    Call<Wallet> sendDenarii(@Path("user") String userName, @Path("wallet") String walletName, @Path("amount") int amountToSend);
}
