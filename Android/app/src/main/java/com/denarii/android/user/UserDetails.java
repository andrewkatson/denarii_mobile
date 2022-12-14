package com.denarii.android.user;

import java.io.Serializable;

public class UserDetails implements Serializable {
    private String userName;
    private String userEmail;
    private String userPassword;
    private WalletDetails walletDetails;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setWalletDetails(WalletDetails walletDetails) {
        this.walletDetails = walletDetails;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public WalletDetails getWalletDetails() {
        return this.walletDetails;
    }
}
