package com.denarii.android.util;

import android.os.Build;

import com.denarii.android.BuildConfig;
import com.denarii.android.constants.Constants;
import com.denarii.android.network.DenariiService;
import com.denarii.android.network.StubbedDenariiService;
import java.util.Objects;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DenariiServiceHandler {

  private static final DenariiService stubbedService = new StubbedDenariiService();

  public static DenariiService returnDenariiService() {

    // Unit tests use this.
    if (Constants.DEBUG) {
      return stubbedService;
    }

    // Instrumented tests and prod use this.
    boolean debug = isDebug();
    if (debug) {
      return stubbedService;
    } else {
      Retrofit retrofit =
          new Retrofit.Builder()
              .baseUrl(Constants.BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build();
      return retrofit.create(DenariiService.class);
    }
  }

  private static boolean isDebug() {

    if (BuildConfig.DEBUG) {
      return true;
    }

    return Build.FINGERPRINT.startsWith("generic")
        || Build.FINGERPRINT.startsWith("unknown")
        || Build.MODEL.contains("google_sdk")
        || Build.MODEL.contains("Emulator")
        || Build.MODEL.contains("Android SDK built for x86")
        || Objects.equals(Build.BOARD, "QC_Reference_Phone") // bluestacks
        || Build.MANUFACTURER.contains("Genymotion")
        || Build.HOST.startsWith("Build") // MSI App Player
        || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        || "google_sdk".equals(Build.PRODUCT);
  }
}
