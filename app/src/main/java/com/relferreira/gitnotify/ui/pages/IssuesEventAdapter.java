package com.relferreira.gitnotify.ui.pages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.Issue;

import java.text.DateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.feras.mdv.MarkdownView;

/**
 * Created by relferreira on 2/12/17.
 */

public class IssuesEventAdapter extends PagesAdapter<IssuesEventAdapter.IssuesViewHolder> {

    private final Context context;
    private final DateFormat dateFormater;
    private List<Issue> items;

    public IssuesEventAdapter(Context context, List<Issue> items) {
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
    public IssuesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_issues, parent, false);
        return new IssuesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IssuesViewHolder holder, int position) {
        Issue issue = items.get(position);

        holder.date.setText(dateFormater.format(issue.createdAt()));
        holder.title.setText(issue.title());
        holder.state.setText(issue.state());
        holder.body.loadMarkdown(issue.body());
        holder.body.setBackgroundColor(0);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class IssuesViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.issue_date)
        TextView date;
        @BindView(R.id.issue_title)
        TextView title;
        @BindView(R.id.issue_state)
        TextView state;
        @BindView(R.id.issue_body)
        MarkdownView body;

        public IssuesViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
