package com.denarii.android.network;

import androidx.annotation.NonNull;

import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.WalletDetails;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StubbedCall implements Call<List<DenariiResponse>> {
    private boolean hasBeenExecuted = false;

    StubbedCall() {
    }

    private DenariiResponse getDenariiResponse() {
        DenariiResponse denariiResponse = new DenariiResponse();
        denariiResponse.userIdentifier = 123;
        denariiResponse.walletAddress = "ABCXYZ";
        denariiResponse.seed = "some seed here";
        denariiResponse.balance = 20;
        return denariiResponse;
    }


    @NonNull
    @Override
    public Response<List<DenariiResponse>> execute() throws IOException {
        hasBeenExecuted = true;
        return Response.success(List.of(getDenariiResponse()));
    }

    @Override
    public void enqueue(Callback callback) {
        Call<List<DenariiResponse>> call = new StubbedCall();
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
