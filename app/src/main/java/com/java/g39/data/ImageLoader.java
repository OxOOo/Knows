package com.java.g39.data;

import android.content.Context;
import android.widget.ImageView;

import com.java.g39.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by equation on 9/11/17.
 */

public class ImageLoader {

    public static void init(Context context) {
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        // FIXME add showImage*()
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(R.mipmap.logo)
                .showImageOnFail(R.mipmap.logo)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .build();
    }

    public static void displayImage(String imageUrl, ImageView imageView) {
        mImageLoader.displayImage(imageUrl, imageView, mDisplayImageOptions);
    }

    private static com.nostra13.universalimageloader.core.ImageLoader
            mImageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
    private static DisplayImageOptions mDisplayImageOptions;
}
