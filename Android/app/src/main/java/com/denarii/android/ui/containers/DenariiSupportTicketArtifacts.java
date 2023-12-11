package com.denarii.android.ui.containers;

import android.widget.Button;
import com.denarii.android.user.SupportTicket;

public class DenariiSupportTicketArtifacts {
    private SupportTicket supportTicket;
    private Button supportTicketButton;

    public void setSupportTicket(SupportTicket supportTicket) {
        this.supportTicket = supportTicket;
    }

    public void setSupportTicketButton(Button supportTicketButton) {
        this.supportTicketButton = supportTicketButton;
    }

    public SupportTicket getSupportTicket() {
        return supportTicket;
    }

    public Button getSupportTicketButton() {
        return supportTicketButton;
    }
}
