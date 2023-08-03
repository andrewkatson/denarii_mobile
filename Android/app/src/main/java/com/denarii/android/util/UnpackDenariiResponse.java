package com.denarii.android.util;

import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.UserDetails;
import com.denarii.android.user.WalletDetails;

import java.util.List;

public class UnpackDenariiResponse {
    public static void unpackLoginOrRegister(UserDetails user, List<DenariiResponse> response){
        user.setUserID(response.get(0).userIdentifier);
        //creating a wallet details so we can set its values in the other scenes
        WalletDetails newWallet = new WalletDetails();
        user.setWalletDetails(newWallet);

    }

    public static void unpackOpenedWallet(UserDetails user, List<DenariiResponse> response)
    {
        user.getWalletDetails().setBalance(response.get(0).balance);
    }

    public static void unpackOpenWallet(UserDetails user, List<DenariiResponse> response)
    {
        user.getWalletDetails().setWalletAddress(response.get(0).walletAddress);
        user.getWalletDetails().setSeed(response.get(0).seed);
    }

    public static void unpackRestoreDeterministicWallet(UserDetails user, List<DenariiResponse> response)
    {
        user.getWalletDetails().setWalletAddress(response.get(0).walletAddress);
    }

    public static void unpackCreateWallet(UserDetails user, List<DenariiResponse> response)
    {
        user.getWalletDetails().setWalletAddress(response.get(0).walletAddress);
        user.getWalletDetails().setSeed(response.get(0).seed);
    }
}
