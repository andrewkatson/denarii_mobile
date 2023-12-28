package com.denarii.android.ui.models;

public class PendingSale {

    private final String askId;
    private final double amount;
    private final double price;
    private final double amountBought;

    public PendingSale(String askId, double amount, double price, double amountBought) {
        this.askId = askId;
        this.amount = amount;
        this.price = price;
        this.amountBought = amountBought;
    }

    public String getAskId() {
        return askId;
    }

    public double getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public double getAmountBought() {
        return amountBought;
    }
}
