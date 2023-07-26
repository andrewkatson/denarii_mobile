package com.denarii.android.network;

import androidx.annotation.NonNull;

import com.denarii.android.user.WalletDetails;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StubbedCall implements Call<List<Wallet>> {
    private boolean hasBeenExecuted = false;

    StubbedCall() {
    }

    private Wallet getWallet() {
        Wallet wallet = new Wallet();
        WalletDetails walletDetails = new WalletDetails();
        walletDetails.userIdentifier = 123;
        walletDetails.walletAddress = "ABCXYZ";
        walletDetails.walletName = "wallet";
        walletDetails.walletPassword = "password";
        walletDetails.seed = "some seed here";
        walletDetails.balance = 20;
        wallet.response = walletDetails;
        wallet.responseCode = 200;
        wallet.responseCodeText = "No error";
        return wallet;
    }


    @NonNull
    @Override
    public Response<List<Wallet>> execute() throws IOException {
        hasBeenExecuted = true;
        return Response.success(List.of(getWallet()));
    }

    @Override
    public void enqueue(Callback callback) {
        Call<List<Wallet>> call = new StubbedCall();
        try {
            callback.onResponse(call, this.execute());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isExecuted() {
        return hasBeenExecuted;
    }

    @Override
    public void cancel() {
        hasBeenExecuted = false;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public Call clone() {
        return null;
    }

    @Override
    public Request request() {
        return null;
    }

    @Override
    public Timeout timeout() {
        return null;
    }
}
