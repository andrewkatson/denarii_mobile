package com.denarii.android.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WalletDetails implements Serializable
{
    @SerializedName("wallet_name")
    public String walletName;

    @SerializedName("wallet_password")
    public String walletPassword;

    @SerializedName("seed")
    public String seed;

    @SerializedName("balance")
    public double balance;

    @SerializedName("wallet_address")
    public String walletAddress;

}
