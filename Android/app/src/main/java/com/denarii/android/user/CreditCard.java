package com.denarii.android.user;

public class CreditCard
{
    private String customerID;

    private String sourceTokenID;

    public void setCustomerID(String newID){customerID = newID;}

    public void setSourceTokenID(String newSourceID){sourceTokenID = newSourceID;}

    public String getCustomerID(){return customerID;}

    public String getSourceTokenID(){return sourceTokenID;}
}
