package com.denarii.android.util;

import android.system.Os;

import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.network.StubbedDenariiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DenariiServiceHandler {

    public static DenariiService returnDenariiService() {
        if (Os.getenv("UI-TESTING").equals("True")) {
            return new StubbedDenariiService();
        } else {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            return retrofit.create(DenariiService.class);
        }
    }
}
