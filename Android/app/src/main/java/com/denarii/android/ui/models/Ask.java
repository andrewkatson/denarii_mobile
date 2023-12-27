package com.denarii.android.ui.models;

public class Ask {

    private final String askId;
    private final double amount;
    private final double price;

    public Ask(String askId, double amount, double price) {
        this.askId = askId;
        this.amount = amount;
        this.price = price;
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
}
