package com.denarii.android.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WalletDetails implements Serializable {
    @SerializedName("balance")
    public double balance;

    @SerializedName("seed")
    public String seed;

    @SerializedName("user_identifier")
    public int userIdentifier;

    @SerializedName("wallet_name")
    public String walletName;

    @SerializedName("wallet_password")
    public String walletPassword;

    @SerializedName("wallet_address")
    public String walletAddress;
}
