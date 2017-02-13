package com.relferreira.gitnotify.ui.pages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.relferreira.gitnotify.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by relferreira on 2/12/17.
 */

public class TextAdapter extends PagesAdapter<TextAdapter.TextViewHolder> {

    private final Context context;
    private List<String> items;

    public TextAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setItems(List items) {
        this.items = items;
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_text, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TextViewHolder holder, int position) {
        String item = items.get(position);
        holder.text.setText(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_item)
        TextView text;

        public TextViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
