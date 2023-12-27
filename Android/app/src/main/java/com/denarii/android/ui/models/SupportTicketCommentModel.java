package com.denarii.android.ui.models;

public class SupportTicketCommentModel {

    private final String author;
    private final String body;

    public SupportTicketCommentModel(String author, String body) {
        this.author = author;
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }
}
