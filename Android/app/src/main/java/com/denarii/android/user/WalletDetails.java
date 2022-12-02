package com.denarii.android.user;

import java.io.Serializable;

public class WalletDetails implements Serializable {
    public double balance;
    public String seed;
    public int userIdentifier;
    public String walletName;
    public String walletPassword;
    public String walletAddress;
}
