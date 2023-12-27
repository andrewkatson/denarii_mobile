package com.denarii.android.ui.recyclerviewadapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.denarii.android.R;

public class SupportTicketCommentRecyclerViewAdapter extends
        RecyclerView.Adapter<SupportTicketCommentRecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView otherAuthor;
        private TextView otherBody;

        private TextView ownBody;

        public ViewHolder(View itemView) {
            super(itemView);

            otherAuthor = itemView.findViewById(R.id.others_comment_author);
            otherBody = itemView.findViewById(R.id.others_comment_body);
            ownBody = itemView.findViewById(R.id.own_comment_body);
        }
    }
}
