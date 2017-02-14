package com.relferreira.gitnotify.ui.pages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.Release;
import com.relferreira.gitnotify.model.ReleaseInfo;
import com.relferreira.gitnotify.model.Repo;

import java.text.DateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by relferreira on 2/13/17.
 */

public class ReleaseAdapter extends PagesAdapter<ReleaseAdapter.ReleaseViewHolder> {

    private final Context context;
    private final DateFormat dateFormater;
    private List<ReleaseInfo> items;

    public ReleaseAdapter(Context context, List<ReleaseInfo> items) {
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
    public ReleaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_release, parent, false);
        return new ReleaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReleaseViewHolder holder, int position) {
        ReleaseInfo releaseInfo = items.get(position);
        Release release = releaseInfo.release();
        Repo repo = releaseInfo.repo();

        holder.date.setText(dateFormater.format(release.publishedAt()));
        holder.title.setText(String.format(context.getString(R.string.action_release), release.tagName(), repo.name()));
        holder.prerelease.setVisibility(release.prerelease() ? View.VISIBLE : View.GONE);
        holder.body.setText(release.body());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ReleaseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.release_item_date)
        TextView date;
        @BindView(R.id.release_item_title)
        TextView title;
        @BindView(R.id.release_item_prerelease)
        TextView prerelease;
        @BindView(R.id.release_item_body)
        TextView body;

        public ReleaseViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
