package com.relferreira.gitnotify.ui.pages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.PullRequest;

import java.text.DateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by relferreira on 2/12/17.
 */

public class PullRequestAdapter extends PagesAdapter<PullRequestAdapter.PullRequestViewHolder> {

    private final DateFormat dateFormater;
    private Context context;
    private List<PullRequest> items;

    public PullRequestAdapter(Context context, List<PullRequest> items) {
        this.context = context;
        this.items = items;

        this.dateFormater = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setItems(List items) {
        this.items = items;
    }

    @Override
    public PullRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pull_request, parent, false);
        return new PullRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PullRequestViewHolder holder, int position) {
        PullRequest item = items.get(position);

        holder.date.setText(dateFormater.format(item.createdAt()));
        holder.commits.setText(context.getString(R.string.pull_request_commits, item.commits()));
        holder.additions.setText(context.getString(R.string.pull_request_additions, item.additions()));
        holder.deletions.setText(context.getString(R.string.pull_request_deletions, item.deletions()));
        holder.body.setText(item.body());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PullRequestViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.pull_request_date)
        TextView date;
        @BindView(R.id.pull_request_commits)
        TextView commits;
        @BindView(R.id.pull_request_additions)
        TextView additions;
        @BindView(R.id.pull_request_deletions)
        TextView deletions;
        @BindView(R.id.pull_request_body)
        TextView body;

        public PullRequestViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
