package com.denarii.android.network;

import androidx.annotation.NonNull;

import com.denarii.android.user.DenariiResponse;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StubbedCall implements Call<List<DenariiResponse>> {
    private final List<DenariiResponse> responses;

    private boolean hasBeenExecuted = false;

    public StubbedCall(List<DenariiResponse> responses) {
        this.responses = responses;
    }

    @NonNull
    @Override
    public Response<List<DenariiResponse>> execute() throws IOException {
        hasBeenExecuted = true;
        // We always return success because the actual view will deal with failure
        return Response.success(responses);
    }

    @Override
    public void enqueue(Callback callback) {
        try {
            callback.onResponse(this, this.execute());
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

    @NonNull
    @Override
    public Call<List<DenariiResponse>> clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new StubbedCall(responses);
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
