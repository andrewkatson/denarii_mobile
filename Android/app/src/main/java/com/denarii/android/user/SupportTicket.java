package com.denarii.android.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SupportTicket implements Serializable {

    private String supportID;

    private String description;

    private String title;

    private boolean resolved;

    private final List<SupportTicketComment> supportTicketCommentList = new ArrayList<>();

    public void setSupportID(String newID) {
        supportID = newID;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public void setResolved(boolean isResolved) {
        resolved = isResolved;
    }

    public void addComment(SupportTicketComment comment) {
        supportTicketCommentList.add(comment);
    }

    public String getSupportID() {
        return supportID;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public boolean getResolved() {
        return resolved;
    }

    public List<SupportTicketComment> getSupportTicketCommentList() {
        return supportTicketCommentList;
    }

    public void clearSupportTicketCommentList() {
        supportTicketCommentList.clear();
    }
}
