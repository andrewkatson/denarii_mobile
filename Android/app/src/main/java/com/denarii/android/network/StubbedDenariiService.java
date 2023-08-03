package com.denarii.android.network;

import com.denarii.android.user.DenariiResponse;

import java.util.List;

import retrofit2.Call;

public class StubbedDenariiService implements DenariiService {
    public Call<List<DenariiResponse>> getUserId(String userName, String email, String password) {
        return new StubbedCall();
    }

    public Call<List<DenariiResponse>> requestPasswordReset(String usernameOrEmail) {
        return new StubbedCall();
    }

    public Call<List<DenariiResponse>> verifyReset(String usernameOrEmail, int resetId) {
        return new StubbedCall();
    }

    public Call<List<DenariiResponse>> resetPassword(String username, String email, String password) {
        return new StubbedCall();
    }

    public Call<List<DenariiResponse>> createWallet(int userIdentifier, String walletName, String password) {
        return new StubbedCall();
    }

    public Call<List<DenariiResponse>> restoreWallet(int userIdentifier, String walletName, String password, String seed) {
        return new StubbedCall();
    }

    public Call<List<DenariiResponse>> openWallet(int userIdentifier, String walletName, String password) {
        return new StubbedCall();
    }

    public Call<List<DenariiResponse>> getBalance(int userIdentifier, String walletName) {
        return new StubbedCall();
    }

    public Call<List<DenariiResponse>> sendDenarii(int userIdentifier, String walletName, String addressToSendTo, double amountToSend) {
        return new StubbedCall();
    }

}
