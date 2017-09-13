package com.java.g39.settings;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.java.g39.R;
import com.java.g39.data.Config;
import com.java.g39.data.Manager;
import com.pchmn.materialchips.ChipView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by equation on 9/13/17.
 */

public class TagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Config.Category> mCategories;
    private OnDeleteClickListener mOnDeleteClickListener;
    private OnTagsCountChangeListener mOnTagsCountChangeListener;
    private Config mConfig;

    public TagsAdapter() {
        mConfig = Manager.I.getConfig();

        mCategories = new ArrayList<Config.Category>();
        mCategories.add(Config.Category.getRecommentCategory());
        mCategories.addAll(mConfig.availableCategories());

        mOnDeleteClickListener = (View view, int position) -> {
            if (mConfig.removeCategory(mCategories.get(position).idx)) {
                mCategories.remove(position);
                notifyItemRemoved(position);
                if (mOnTagsCountChangeListener != null)
                    mOnTagsCountChangeListener.onTagsCountChange(mCategories.size());
            }
        };
    }

    public void addTag(int idx) {
        Config.Category c = mConfig.addCategory(idx);
        if (c != null) {
            mCategories.add(c);
            notifyItemInserted(mCategories.size() - 1);
            if (mOnTagsCountChangeListener != null)
                mOnTagsCountChangeListener.onTagsCountChange(mCategories.size());
        }
    }

    public void setOnTagsCountChangeListener(OnTagsCountChangeListener listener) {
        mOnTagsCountChangeListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new TagsAdapter.TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final TagViewHolder tag = (TagViewHolder) holder;
        tag.mTag.setLabel(mCategories.get(position).title);
        tag.mTag.setDeletable(position > 0);
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    interface OnDeleteClickListener {
        void onDeleteTag(View view, int position);
    }

    interface OnTagsCountChangeListener {
        void onTagsCountChange(int count);
    }

    class TagViewHolder extends RecyclerView.ViewHolder {
        ChipView mTag;

        public TagViewHolder(View view) {
            super(view);
            mTag = (ChipView) view.findViewById(R.id.tag);
            mTag.setOnDeleteClicked((View v) -> {
                mOnDeleteClickListener.onDeleteTag(view, this.getLayoutPosition());
            });
        }
    }

}
