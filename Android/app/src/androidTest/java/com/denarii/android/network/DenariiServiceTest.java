package com.denarii.android.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.denarii.android.constants.Constants;
import com.denarii.android.user.DenariiResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(AndroidJUnit4.class)
public class DenariiServiceTest {

    Retrofit retrofit;
    DenariiService denariiService;

    @Before
    public void setUp() {
        retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        denariiService = retrofit.create(DenariiService.class);
    }

    @Test
    public void getUserId_fails() {

        Call<List<DenariiResponse>> wallet = denariiService.getUserId("username","email@email.com", "password");

        try {
            Response<List<DenariiResponse>> response = wallet.execute();
            assert response.body() != null;
            DenariiResponse responseWallet = response.body().get(0);

            assertTrue(response.isSuccessful());
            assertEquals(Objects.requireNonNull(responseWallet).userIdentifier, 1);
        } catch (IOException e) {
            assertEquals(e.getMessage(), "Unable to resolve host \"denariimobilebackend.com\": No address associated with hostname");
        }
    }

    @Test
    public void requestReset_fails() {

        Call<List<DenariiResponse>> wallet = denariiService.requestPasswordReset("username");

        try {
            Response<List<DenariiResponse>> response = wallet.execute();
            assert response.body() != null;
            DenariiResponse responseWallet = response.body().get(0);

            assertTrue(response.isSuccessful());
            assertEquals(Objects.requireNonNull(responseWallet).userIdentifier, 1);
        } catch (IOException e) {
            assertEquals(e.getMessage(), "Unable to resolve host \"denariimobilebackend.com\": No address associated with hostname");
        }
    }

    @Test
    public void verifyReset_fails() {

        Call<List<DenariiResponse>> wallet = denariiService.verifyReset("username", 123);

        try {
            Response<List<DenariiResponse>> response = wallet.execute();
            assert response.body() != null;
            DenariiResponse responseWallet = response.body().get(0);

            assertTrue(response.isSuccessful());
            assertEquals(Objects.requireNonNull(responseWallet).userIdentifier, 1);
        } catch (IOException e) {
            assertEquals(e.getMessage(), "Unable to resolve host \"denariimobilebackend.com\": No address associated with hostname");
        }
    }


    @Test
    public void getResetPassword_fails() {

        Call<List<DenariiResponse>> wallet = denariiService.resetPassword("username", "email","password");

        try {
            Response<List<DenariiResponse>> response = wallet.execute();
            assert response.body() != null;
            DenariiResponse responseWallet = response.body().get(0);

            assertTrue(response.isSuccessful());
            assertEquals(Objects.requireNonNull(responseWallet).userIdentifier, 1);
        } catch (IOException e) {
            assertEquals(e.getMessage(), "Unable to resolve host \"denariimobilebackend.com\": No address associated with hostname");
        }
    }


    @Test
    public void createWallet_fails() {
        Call<List<DenariiResponse>> wallet = denariiService.createWallet(1, "wallet_name", "wallet_password");

        try {
            Response<List<DenariiResponse>> response = wallet.execute();
            assert response.body() != null;
            DenariiResponse responseWallet = response.body().get(0);

            assertTrue(response.isSuccessful());
            assertEquals(Objects.requireNonNull(responseWallet).seed, "do re me");
            assertEquals(Objects.requireNonNull(responseWallet).walletAddress, "wallet_address");
        } catch (IOException e) {
            assertEquals(e.getMessage(), "Unable to resolve host \"denariimobilebackend.com\": No address associated with hostname");
        }
    }

    @Test
    public void openWallet_fails() {
        Call<List<DenariiResponse>> wallet = denariiService.openWallet(1, "wallet_name", "wallet_password");

        try {
            Response<List<DenariiResponse>> response = wallet.execute();
            assert response.body() != null;
            DenariiResponse responseWallet = response.body().get(0);

            assertTrue(response.isSuccessful());
            assertEquals(Objects.requireNonNull(responseWallet).seed, "do re me");
            assertEquals(Objects.requireNonNull(responseWallet).walletAddress, "wallet_address");
        } catch (IOException e) {
            assertEquals(e.getMessage(), "Unable to resolve host \"denariimobilebackend.com\": No address associated with hostname");
        }
    }

    @Test
    public void restoreWallet_fails() {
        Call<List<DenariiResponse>> wallet = denariiService.restoreWallet(1, "wallet_name", "wallet_password", "do re me");

        try {
            Response<List<DenariiResponse>> response = wallet.execute();
            assert response.body() != null;
            DenariiResponse responseWallet = response.body().get(0);

            assertTrue(response.isSuccessful());
            assertEquals(Objects.requireNonNull(responseWallet).walletAddress, "wallet_address");
        } catch (IOException e) {
            assertEquals(e.getMessage(), "Unable to resolve host \"denariimobilebackend.com\": No address associated with hostname");
        }
    }

    @Test
    public void getBalance_fails() {
        Call<List<DenariiResponse>> wallet = denariiService.getBalance(1, "wallet_name");

        try {
            Response<List<DenariiResponse>> response = wallet.execute();
            assert response.body() != null;
            DenariiResponse responseWallet = response.body().get(0);

            assertTrue(response.isSuccessful());
            assertEquals(Objects.requireNonNull(responseWallet).walletAddress, "wallet_address");
            assertEquals(Objects.requireNonNull(responseWallet).balance, 0.0, 0.0);
        } catch (IOException e) {
            assertEquals(e.getMessage(), "Unable to resolve host \"denariimobilebackend.com\": No address associated with hostname");
        }
    }

    @Test
    public void sendDenarii_fails() {
        Call<List<DenariiResponse>> wallet = denariiService.sendDenarii(1, "wallet_name", "other_address", 1.0);

        try {
            Response<List<DenariiResponse>> response = wallet.execute();
            assert response.body() != null;
            DenariiResponse responseWallet = response.body().get(0);

            assertTrue(response.isSuccessful());
            assertEquals(Objects.requireNonNull(responseWallet).balance, 0.0, 0.0);
        } catch (IOException e) {
            assertEquals(e.getMessage(), "Unable to resolve host \"denariimobilebackend.com\": No address associated with hostname");
        }
    }
}
