package com.denarii.android.user;

import java.io.Serializable;

public class WalletDetails implements Serializable
{
    private String walletName;

    private String walletPassword;

    private String seed;

    private double balance;

    private String walletAddress;

    public String getWalletName(){return walletName;}

    public String getWalletPassword(){return walletPassword;}

    public String getSeed(){return seed;}

    public double getBalance(){return balance;}

    public String getWalletAddress(){return walletAddress;}

    public void setWalletName(String newName){walletName = newName;}

    public void setWalletPassword(String newPassword){walletPassword = newPassword;}

    public void setSeed(String newSeed){seed = newSeed;}

    public void setBalance(double newBalance){balance = newBalance;}

    public void setWalletAddress(String newAddress){walletAddress = newAddress;}

}
