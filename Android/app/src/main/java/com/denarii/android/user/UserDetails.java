package com.denarii.android.user;

import java.io.Serializable;
import java.util.List;

public class UserDetails implements Serializable {
    private String userName;
    private String userEmail;
    private String userPassword;
    private WalletDetails walletDetails;
    private CreditCard creditCard;
    private List<SupportTicket> supportTicketList;
    private List<DenariiAsk> denariiAskList;
    private int userID;

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
    public void setUserID(int newID){ userID = newID;}
    public void addSupportTicket(SupportTicket newTicket){
        supportTicketList.add(newTicket);
    }
    public void setCreditCard(CreditCard newCard){
        creditCard = newCard;
    }
    public void addDenariiAsk(DenariiAsk newAsk){
        denariiAskList.add(newAsk);
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

    public int getUserID(){return userID;}

    public List<SupportTicket> getSupportTicketList() {return supportTicketList;}

    public CreditCard getCreditCard(){return creditCard;}

    public List<DenariiAsk> getDenariiAskList(){return denariiAskList;}
}
