package com.denarii.android.network;

import com.denarii.android.user.Wallet;

import java.util.List;

import retrofit2.Call;

public class StubbedDenariiService implements DenariiService {
    public Call<List<Wallet>> getUserId(String userName, String email, String password) {
        return new StubbedCall();
    }

    public Call<List<Wallet>> requestPasswordReset(String usernameOrEmail) {
        return new StubbedCall();
    }

    public Call<List<Wallet>> verifyReset(String usernameOrEmail, int resetId) {
        return new StubbedCall();
    }

    public Call<List<Wallet>> resetPassword(String username, String email, String password) {
        return new StubbedCall();
    }

    public Call<List<Wallet>> createWallet(int userIdentifier, String walletName, String password) {
        return new StubbedCall();
    }

    public Call<List<Wallet>> restoreWallet(int userIdentifier, String walletName, String password, String seed) {
        return new StubbedCall();
    }

    public Call<List<Wallet>> openWallet(int userIdentifier, String walletName, String password) {
        return new StubbedCall();
    }

    public Call<List<Wallet>> getBalance(int userIdentifier, String walletName) {
        return new StubbedCall();
    }

    public Call<List<Wallet>> sendDenarii(int userIdentifier, String walletName, String addressToSendTo, double amountToSend) {
        return new StubbedCall();
    }

    public DenariiService getInstance() {
        return new StubbedDenariiService();
    }
}
