package com.denarii.android.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DenariiAsk implements Serializable
{
    @SerializedName("ask_id")
    public String askID;

    @SerializedName("amount")
    public double amount;

    @SerializedName("asking_price")
    public double askingPrice;

    @SerializedName("in_escrow")
    public boolean inEscrow;

    @SerializedName("amount_bought")
    public double amountBought;

    @SerializedName("is_settled")
    public boolean isSettled;

    @SerializedName("has_been_seen_by_seller")
    public boolean hasBeenSeenBySeller;
}
