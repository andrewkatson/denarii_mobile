package com.denarii.android.network;

import com.denarii.android.constants.Constants;
import com.denarii.android.user.Wallet;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DenariiServiceImpl implements DenariiService{

    private DenariiService denariiService;

    public Call<List<Wallet>> getUserId(String userName, String email, String password) {
        return denariiService.getUserId(userName, email, password);
    }

    public Call<List<Wallet>> requestPasswordReset(String usernameOrEmail) {
        return denariiService.requestPasswordReset(usernameOrEmail);
    }

    public Call<List<Wallet>> verifyReset(String usernameOrEmail, int resetId) {
        return denariiService.verifyReset(usernameOrEmail, resetId);
    }

    public Call<List<Wallet>> resetPassword(String username, String email, String password) {
        return denariiService.resetPassword(username, email, password);
    }

    public Call<List<Wallet>> createWallet(int userIdentifier, String walletName, String password) {
        return denariiService.createWallet(userIdentifier, walletName, password);
    }

    public Call<List<Wallet>> restoreWallet(int userIdentifier, String walletName, String password, String seed) {
        return denariiService.restoreWallet(userIdentifier, walletName, password, seed);
    }

    public Call<List<Wallet>> openWallet(int userIdentifier, String walletName, String password) {
        return denariiService.openWallet(userIdentifier, walletName, password);
    }

    public Call<List<Wallet>> getBalance(int userIdentifier, String walletName) {
        return denariiService.getBalance(userIdentifier, walletName);
    }

    public Call<List<Wallet>> sendDenarii(int userIdentifier, String walletName, String addressToSendTo, double amountToSend)
    {
        return denariiService.sendDenarii(userIdentifier, walletName, addressToSendTo, amountToSend);
    }

    public DenariiService getInstance() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        DenariiService denariiService = retrofit.create(DenariiService.class);
        this.denariiService = denariiService;
        return denariiService;
    }
}
