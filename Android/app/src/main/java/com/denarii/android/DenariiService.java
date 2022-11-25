package com.denarii.android;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DenariiService {

    @POST("users/{user}/{email}/{password}/create")
    Call<Wallet> createWallet(@Path("user") String userName, @Path("email") String email, @Path("password") String password);

    @PATCH("users/{user}/{email}/{wallet}/{password}/{seed}/restore")
    Call<Wallet> restoreWallet(@Path("user") String userName, @Path("email") String email, @Path("wallet") String walletName, @Path("password") String password, @Path("seed") String seed);

    @GET("users/{user}/{email}/{wallet}/{password}/open")
    Call<Wallet> openWallet(@Path("user") String userName, @Path("email") String email, @Path("wallet") String walletName, @Path("password") String password);

    @GET("users/{user}/{email}/{wallet}/balance")
    Call<Wallet> getBalance(@Path("user") String userName, @Path("email") String email, @Path("wallet") String walletName);

    @POST("users/{user}/{email}/{wallet}/{amount}/send")
    Call<Wallet> sendDenarii(@Path("user") String userName, @Path("email") String email, @Path("wallet") String walletName, @Path("amount") int amountToSend);
}
