package com.relferreira.gitnotify.ui.pages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.Commit;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by relferreira on 2/12/17.
 */

public class PushAdapter extends PagesAdapter<PushAdapter.PushViewHolder> {

    private final Context context;
    private List<Commit> items;

    public PushAdapter(Context context, List<Commit> items) {
        this.context = context;
        this.items = items;
    }
    @SuppressWarnings("unchecked")
    @Override
    public void setItems(List items) {
        this.items = items;
    }

    @Override
    public PushViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_push, parent, false);
        return new PushViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PushViewHolder holder, int position) {
        Commit commit = items.get(position);

        holder.author.setText(commit.author().name());
        holder.title.setText(commit.message());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PushViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.push_author)
        TextView author;
        @BindView(R.id.push_title)
        TextView title;

        public PushViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
