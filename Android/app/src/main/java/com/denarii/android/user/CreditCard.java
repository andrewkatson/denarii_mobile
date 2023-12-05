package com.denarii.android.user;

import java.io.Serializable;

public class CreditCard implements Serializable {
  private String customerID;

  private String sourceTokenID;

  private boolean hasCreditCardInfo = false;

  public void setCustomerID(String newID) {
    customerID = newID;
  }

  public void setSourceTokenID(String newSourceID) {
    sourceTokenID = newSourceID;
  }

  public void setHasCreditCardInfo(boolean hasCreditCardInfo) {
    this.hasCreditCardInfo = hasCreditCardInfo;
  }

  public String getCustomerID() {
    return customerID;
  }

  public String getSourceTokenID() {
    return sourceTokenID;
  }

  public boolean getHasCreditCardInfo() {
    return hasCreditCardInfo;
  }
}
