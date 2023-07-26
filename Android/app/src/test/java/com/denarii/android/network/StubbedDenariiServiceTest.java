package com.denarii.android.network;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;

import com.denarii.android.constants.Constants;
import com.denarii.android.user.WalletDetails;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StubbedDenariiServiceTest {

    @BeforeClass
    public static void beforeClass() {
        Constants.DEBUG = true;
    }

    private StubbedDenariiService stubbedDenariiService;

    @Before
    public void setup() {
        stubbedDenariiService = new StubbedDenariiService();
    }

    @Test
    public void getUserId_returnsWalletWithUserId() {
        Call<List<Wallet>> wallets = stubbedDenariiService.getUserId("user", "email@email.com", "password");

        wallets.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        WalletDetails walletDetails = response.body().get(0).response;

                        assertEquals(123, walletDetails.userIdentifier);
                    } else {
                        throw new IllegalStateException("Response Body Should never be null");
                    }
                } else {
                    throw new IllegalStateException("Response Body Should never be unsuccessful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                throw new IllegalStateException("Response should never fail");
            }
        });
    }

    @Test
    public void requestReset_requestsReset() {
        Call<List<Wallet>> wallets = stubbedDenariiService.requestPasswordReset("user");

        wallets.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                assertTrue(response.isSuccessful());
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                throw new IllegalStateException("Response should never fail");
            }
        });
    }

    @Test
    public void verifyReset_verifiesReset() {
        Call<List<Wallet>> wallets = stubbedDenariiService.verifyReset("user", 123);

        wallets.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                assertTrue(response.isSuccessful());
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                throw new IllegalStateException("Response should never fail");
            }
        });
    }

    @Test
    public void resetPassword_resetsPassword() {
        Call<List<Wallet>> wallets = stubbedDenariiService.resetPassword("user", "email@email.com", "password");

        wallets.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                assertTrue(response.isSuccessful());
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                throw new IllegalStateException("Response should never fail");
            }
        });
    }

    @Test
    public void createWallet_createsWallet() {
        Call<List<Wallet>> wallets = stubbedDenariiService.createWallet(123, "wallet", "password");

        wallets.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        WalletDetails walletDetails = response.body().get(0).response;

                        assertEquals("some seed here", walletDetails.seed);
                        assertEquals("ABCXYZ", walletDetails.walletAddress);
                    } else {
                        throw new IllegalStateException("Response Body Should never be null");
                    }
                } else {
                    throw new IllegalStateException("Response Body Should never be unsuccessful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                throw new IllegalStateException("Response should never fail");
            }
        });
    }

    @Test
    public void openWallet_opensWallet() {
        Call<List<Wallet>> wallets = stubbedDenariiService.openWallet(123, "wallet", "password");

        wallets.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        WalletDetails walletDetails = response.body().get(0).response;

                        assertEquals("some seed here", walletDetails.seed);
                        assertEquals("ABCXYZ", walletDetails.walletAddress);
                    } else {
                        throw new IllegalStateException("Response Body Should never be null");
                    }
                } else {
                    throw new IllegalStateException("Response Body Should never be unsuccessful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                throw new IllegalStateException("Response should never fail");
            }
        });
    }

    @Test
    public void restoreWallet_restoresWallet() {
        Call<List<Wallet>> wallets = stubbedDenariiService.restoreWallet(123, "wallet", "password", "some seed here");

        wallets.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        WalletDetails walletDetails = response.body().get(0).response;

                        assertEquals("ABCXYZ", walletDetails.walletAddress);
                    } else {
                        throw new IllegalStateException("Response Body Should never be null");
                    }
                } else {
                    throw new IllegalStateException("Response Body Should never be unsuccessful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                throw new IllegalStateException("Response should never fail");
            }
        });
    }

    @Test
    public void getBalance_getsBalance() {
        Call<List<Wallet>> wallets = stubbedDenariiService.getBalance(123, "wallet");

        wallets.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        WalletDetails walletDetails = response.body().get(0).response;

                        assertEquals(20, walletDetails.balance, 1);
                    } else {
                        throw new IllegalStateException("Response Body Should never be null");
                    }
                } else {
                    throw new IllegalStateException("Response Body Should never be unsuccessful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                throw new IllegalStateException("Response should never fail");
            }
        });
    }

    @Test
    public void sendDenarii_sendsDenarii() {
        Call<List<Wallet>> wallets = stubbedDenariiService.sendDenarii(123, "wallet", "XYZABC", 10);

        wallets.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {
                assertTrue(response.isSuccessful());
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>> call, @NonNull Throwable t) {
                throw new IllegalStateException("Response should never fail");
            }
        });
    }
}
