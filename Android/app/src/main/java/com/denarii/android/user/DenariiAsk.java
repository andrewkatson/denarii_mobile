package com.denarii.android.user;

import java.io.Serializable;

public class DenariiAsk implements Serializable
{
    private String askID;

    private double amount;

    private double askingPrice;

    private boolean inEscrow;

    private double amountBought;

    private boolean isSettled;

    private boolean seenBySeller;

    public void setAskID(String newID){ askID = newID;}

    public void setAmount(double newAmount){ amount = newAmount;}

    public void setAskingPrice(double newAskingPrice){ askingPrice = newAskingPrice;}

    public void setInEscrow(boolean newEscrow){ inEscrow = newEscrow;}

    public void setAmountBought(double newAmountBought){ amountBought = newAmountBought;}

    public void setIsSettled(boolean newSettled){ isSettled = newSettled;}

    public void setHasBeenSeenBySeller(boolean newSeller){ seenBySeller = newSeller;}

    public String getAskID(){ return askID;}

    public double getAmount(){return amount;}

    public double getAskingPrice(){return askingPrice;}

    public boolean getInEscrow(){return inEscrow;}

    public double getAmountBought(){ return amountBought;}

    public boolean getIsSettled(){return isSettled;}

    public boolean getSeenBySeller(){return seenBySeller;}


}
