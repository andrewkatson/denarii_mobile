package com.denarii.android.user;

import java.io.Serializable;

public class CreditCard implements Serializable
{
    private String customerID;

    private String sourceTokenID;

    public void setCustomerID(String newID){customerID = newID;}

    public void setSourceTokenID(String newSourceID){sourceTokenID = newSourceID;}

    public String getCustomerID(){return customerID;}

    public String getSourceTokenID(){return sourceTokenID;}
}
