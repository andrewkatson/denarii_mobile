package com.denarii.android.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DenariiResponse implements Serializable {

    @SerializedName("has_credit_card_info")
    public boolean hasCreditCardInfo;

    @SerializedName("balance")
    public double balance;

    @SerializedName("seed")
    public String seed;

    @SerializedName("user_identifier")
    public int userIdentifier;

    @SerializedName("wallet_address")
    public String walletAddress;

    @SerializedName("ask_id")
    public String askID;

    @SerializedName("amount")
    public double amount;

    @SerializedName("asking_price")
    public double askingPrice;

    @SerializedName("amount_bought")
    public double amountBought;

    @SerializedName("transaction_was_settled")
    public boolean transactionWasSettled;
}
