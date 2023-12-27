package com.denarii.android.ui.models;

public class SupportTicketModel {
    private final String supportTicketId;
    private final String title;
    private final String description;

    public SupportTicketModel(String supportTicketId, String title, String description) {
        this.supportTicketId = supportTicketId;
        this.title = title;
        this.description = description;
    }

    public String getSupportTicketId() {
        return supportTicketId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
