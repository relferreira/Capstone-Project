package com.relferreira.gitnotify.ui.pages;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.Comment;
import com.relferreira.gitnotify.util.RoundBitmapHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by relferreira on 2/11/17.
 */

public class IssueCommentAdapter extends PagesAdapter<IssueCommentAdapter.IssueCommentViewHolder> {

    private final DateFormat dateFormater;
    private Context context;
    private List<Comment> comments;

    public IssueCommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;

        this.dateFormater = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    }

    @Override
    public IssueCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_issue_comment, parent, false);
        return new IssueCommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(IssueCommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.text.setText(comment.body());
        holder.date.setText(dateFormater.format(comment.createdAt()));
        Picasso.with(context)
                .load(String.format("%1$sv=3&s=60", comment.user().avatarUrl()))
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        BitmapDrawable img = ((BitmapDrawable) holder.image.getDrawable());
                        holder.image.setImageDrawable(RoundBitmapHelper.getRoundImage(img, context.getResources()));
                    }

                    @Override
                    public void onError() {
                        // TODO error image
                    }
                });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @SuppressWarnings("unchecked")
    public void setItems(List items) {
        comments = (List<Comment>)items;
        notifyDataSetChanged();
    }

    public class IssueCommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.issue_comment_date)
        TextView date;
        @BindView(R.id.issue_comment_text)
        TextView text;
        @BindView(R.id.issue_comment_user_image)
        ImageView image;

        public IssueCommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}