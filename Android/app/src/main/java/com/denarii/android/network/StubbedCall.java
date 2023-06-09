package com.denarii.android.network;

import androidx.annotation.NonNull;

import com.denarii.android.user.Wallet;
import com.google.gson.Gson;

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
        String responseStr = "{ \"responseCode\": 200\", \"responseCodeText\": \"No error\", \"response\": { \"balance\": 20, \"seed\": \"some seed here\", \"userIdentifier\": \"user\", \"walletName\": \"wallet\", \"walletPassword\": \"password\", \"walletAddress\": \"ABCXYZ\"}}";
        return new Gson().fromJson(responseStr, Wallet.class);
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
