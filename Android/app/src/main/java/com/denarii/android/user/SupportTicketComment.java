package com.denarii.android.user;

import java.io.Serializable;

public class SupportTicketComment implements Serializable {
    private String author;

    private String content;

    public void setAuthor(String newAuthor) {
        author = newAuthor;
    }

    public void setContent(String newContent) {
        content = newContent;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
