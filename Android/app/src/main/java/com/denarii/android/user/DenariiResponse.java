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
    public String userIdentifier;
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

    @SerializedName("verification_status")
    public String verificationStatus;

    @SerializedName("author")
    public String author;

    @SerializedName("content")
    public String content;

    @SerializedName("description")
    public String description;

    @SerializedName("title")
    public String title;

    @SerializedName("support_ticket_id")
    public String supportTicketID;

    @SerializedName("is_resolved")
    public boolean isResolved;

    @SerializedName("creation_time_body")
    public String creationTimeBody;

    @SerializedName("updated_time_body")
    public String updatedTimeBody;
}
