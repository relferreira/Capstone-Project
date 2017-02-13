package com.relferreira.gitnotify.ui.pages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.Page;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by relferreira on 2/12/17.
 */

public class WikiAdapter extends PagesAdapter<WikiAdapter.WikiViewHolder> {

    private final Context context;
    private List<Page> items;

    public WikiAdapter(Context context, List<Page> items) {
        this.context = context;
        this.items = items;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setItems(List items) {
        this.items = items;
    }

    @Override
    public WikiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_wiki, parent, false);
        return new WikiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WikiViewHolder holder, int position) {
        Page page = items.get(position);

        holder.name.setText(page.action());
        holder.title.setText(page.title());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class WikiViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.page_item_name)
        TextView name;
        @BindView(R.id.page_item_title)
        TextView title;

        public WikiViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
