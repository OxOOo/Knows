package com.java.g39.news.newslist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.java.g39.R;
import com.java.g39.data.SimpleNews;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * 新闻列表适配器
 * Created by equation on 9/8/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;

    private Context mContext;
    private List<SimpleNews> mData = new ArrayList<SimpleNews>();
    private boolean mIsShowFooter = true;
    private OnItemClickListener mOnItemClickListener;
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mDisplayOptions;

    public NewsAdapter(Context context) {
        mContext = context;
        mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        // FIXME add showImage*()
        mDisplayOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(R.mipmap.logo)
                .showImageOnFail(R.mipmap.logo)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .build();
    }

    public SimpleNews getNews(int position) {
        return mData.get(position);
    }

    public void setData(List<SimpleNews> data) {
        mData = data;
        this.notifyDataSetChanged();
    }

    public void appendData(List<SimpleNews> data) {
        int pos = mData.size();
        mData.addAll(data);
        this.notifyItemInserted(pos);
    }

    public void setRead(int position) {
        SimpleNews news = getNews(position);
        if (!news.has_read) {
            news.has_read = true;
            mData.set(position, news);
        }
    }

    public boolean isShowFooter() {
        return mIsShowFooter;
    }

    public void setFooterVisible(boolean visible) {
        mIsShowFooter = visible;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            SimpleNews news = mData.get(position);
            final ItemViewHolder item = (ItemViewHolder) holder;
            item.mTitle.setText(news.news_Title);
            item.mAuthor.setText(news.news_Author.isEmpty() ? news.news_Source : news.news_Author);
            item.mDate.setText(news.news_Time.length() != 14 ? "0000-00-00" : news.news_Time.substring(0, 4) + "-" + news.news_Time.substring(4, 6) + "-" + news.news_Time.substring(6, 8));
            item.mImage.setImageBitmap(null);
            item.setBackgroundColor(mContext.getResources().getColor(news.has_read ? R.color.colorCardRead : R.color.colorCard));
            news.picture_url
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            mImageLoader.displayImage(s, item.mImage, mDisplayOptions);
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mData.size() && mIsShowFooter)
            return TYPE_FOOTER;
        else
            return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mData.size() + (mIsShowFooter ? 1 : 0);
    }

    /**
     * 新闻点击 Listener
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 新闻单元格
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;
        TextView mTitle, mAuthor, mDate;
        ImageView mImage;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.text_title);
            mAuthor = (TextView) view.findViewById(R.id.text_author);
            mDate = (TextView) view.findViewById(R.id.text_date);
            mImage = (ImageView) view.findViewById(R.id.image_view);
            view.setOnClickListener(this);
        }

        public void setBackgroundColor(int color) {
            mView.setBackgroundColor(color);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, this.getLayoutPosition());
            }
        }
    }

    /**
     * 列表底部
     */
    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }
    }
}
