package com.denarii.android.network;

import com.denarii.android.user.Wallet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DenariiService {

    @GET("users/{user}/{email}/{password}")
    Call<List<Wallet>> getUserId(@Path("user") String userName, @Path("email") String email, @Path("password") String password);

    @POST("users/{id}/{wallet}/{password}/create")
    Call<List<Wallet>> createWallet(@Path("id") int userIdentifier, @Path("wallet") String walletName, @Path("password") String password);

    @PATCH("users/{id}/{wallet}/{password}/{seed}/restore")
    Call<List<Wallet>> restoreWallet(@Path("id") int userIdentifier, @Path("wallet") String walletName, @Path("password") String password, @Path("seed") String seed);

    @GET("users/{id}/{wallet}/{password}/open")
    Call<List<Wallet>> openWallet(@Path("id") int userIdentifier, @Path("wallet") String walletName, @Path("password") String password);

    @GET("users/{id}/{wallet}/balance")
    Call<List<Wallet>> getBalance(@Path("id") int userIdentifier, @Path("wallet") String walletName);

    @POST("users/{id}/{wallet}/{address}/{amount}/send")
    Call<List<Wallet>> sendDenarii(@Path("id") int userIdentifier, @Path("wallet") String walletName, @Path("address") String addressToSendTo, @Path("amount") double amountToSend);
}
