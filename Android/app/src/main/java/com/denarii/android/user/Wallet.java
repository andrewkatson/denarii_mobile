package com.denarii.android.user;

import com.google.gson.annotations.SerializedName;

public class Wallet {

    @SerializedName("response_code")
    public int responseCode;

    @SerializedName("response_code_text")
    public String responseCodeText;

    @SerializedName("response")
    public WalletDetails response;
}
