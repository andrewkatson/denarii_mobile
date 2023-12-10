package com.denarii.android.ui.containers;

import android.widget.TextView;
import com.denarii.android.user.SupportTicketComment;

public class DenariiCommentArtifacts {
  private TextView bodyTextView;

  private SupportTicketComment supportTicketComment;

  public void setBodyTextView(TextView bodyTextView) {
    this.bodyTextView = bodyTextView;
  }

  public void setSupportTicketComment(SupportTicketComment supportTicketComment) {
    this.supportTicketComment = supportTicketComment;
  }

  public TextView getBodyTextView() {
    return bodyTextView;
  }

  public SupportTicketComment getSupportTicketComment() {
    return supportTicketComment;
  }
}
