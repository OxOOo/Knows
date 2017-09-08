package com.java.g39.news.newslist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.g39.R;
import com.java.g39.data.SimpleNews;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻列表适配器
 * Created by equation on 9/8/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<SimpleNews> mData = new ArrayList<SimpleNews>();

    public NewsAdapter() {
    }

    public NewsAdapter(List<SimpleNews> data) {
        mData = data;
    }

    public void setData(List<SimpleNews> data) {
        mData = data;
        this.notifyDataSetChanged();
    }

    public void appendData(List<SimpleNews> data) {
        mData.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SimpleNews news = mData.get(position);
        ItemViewHolder item = (ItemViewHolder) holder;
        item.mTitle.setText(news.news_Title);
        item.mAuthor.setText(news.news_Author.isEmpty() ? news.news_Source : news.news_Author);
        item.mDate.setText(news.news_Time);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 新闻单元格
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle, mAuthor, mDate;
        ImageView mImage;

        public ItemViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.text_title);
            mAuthor = (TextView) view.findViewById(R.id.text_author);
            mDate = (TextView) view.findViewById(R.id.text_date);
            mImage = (ImageView) view.findViewById(R.id.image_view);
        }
    }
}
