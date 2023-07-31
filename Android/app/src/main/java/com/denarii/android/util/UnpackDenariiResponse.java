package com.denarii.android.util;

import com.denarii.android.user.DenariiResponse;
import com.denarii.android.user.UserDetails;
import com.denarii.android.user.WalletDetails;

import java.util.List;

public class UnpackDenariiResponse {
    public static void unpackLogin(UserDetails user, List<DenariiResponse> response){
        user.setUserID(response.get(0).userID);

    }
}
