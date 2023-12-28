package com.denarii.android.ui.recyclerviewadapters;

import static com.google.android.material.color.MaterialColors.getColor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.denarii.android.R;
import com.denarii.android.ui.models.SupportTicketCommentModel;

import java.util.List;
import java.util.Objects;

public class SupportTicketCommentRecyclerViewAdapter extends
        RecyclerView.Adapter<SupportTicketCommentRecyclerViewAdapter.ViewHolder> {

    private final List<SupportTicketCommentModel> comments;
    private final String user;

    public SupportTicketCommentRecyclerViewAdapter(List<SupportTicketCommentModel> comments, String user) {
        this.comments = comments;
        this.user = user;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public SupportTicketCommentRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.support_ticket_comment, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    @SuppressWarnings("ResourceType")
    public void onBindViewHolder(SupportTicketCommentRecyclerViewAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        SupportTicketCommentModel commentModel = comments.get(position);

        // Set item views based on your views and data model
        TextView author = holder.author;
        if (Objects.equals(user, commentModel.getAuthor())) {
            author.setVisibility(View.INVISIBLE);
        } else {
            author.setText(commentModel.getAuthor());
        }

        TextView body = holder.body;
        // If this is an author comment move the body to the right and change the color
        if (Objects.equals(user, commentModel.getAuthor())) {
            ConstraintLayout constraintLayout = holder.itemView.findViewById(R.id.support_ticket_comment_layout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(body.getId(), ConstraintSet.LEFT);
            constraintSet.connect(body.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT, 0);
            constraintSet.applyTo(constraintLayout);

            body.setBackgroundColor(getColor(holder.itemView, R.color.teal_200));
        }
        body.setText(commentModel.getBody());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView author;
        private final TextView body;
        private final View itemView;

        public ViewHolder(View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.comment_author);
            body = itemView.findViewById(R.id.comment_body);
            this.itemView = itemView;
        }
    }
}
