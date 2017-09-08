package com.java.g39.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyu on 2017/9/7.
 * 磁盘
 */

class FS {
    private SQLiteDatabase db;

    private static final String TABLE_NAME_CATEGORY = "news_category";
    private static final String TABLE_NAME_DETAIL = "news_detail";
    private static final String KEY_ID = "news_id";
    private static final String KEY_SIMPLE = "simple_json";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_DETAIL = "detail_json";

    FS(Context context) {
        this.db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().getPath() + "/data.db",null);
        // dropTables(); // FIXME
        createTables();
    }

    public void createTables() {
        final String category_table = String.format("create table if not exists `%s`(%s string primary key, %s text, %s integer)", TABLE_NAME_CATEGORY, KEY_ID, KEY_SIMPLE, KEY_CATEGORY);
        final String detail_table = String.format("create table if not exists `%s`(%s string primary key, %s text)", TABLE_NAME_DETAIL, KEY_ID, KEY_DETAIL);
        db.execSQL(category_table);
        db.execSQL(detail_table);
    }

    public void dropTables() {
        db.execSQL(String.format("drop table if exists `%s`", TABLE_NAME_CATEGORY));
        db.execSQL(String.format("drop table if exists `%s`", TABLE_NAME_DETAIL));
    }

    public void insertSimple(SimpleNews simpleNews, int category) {
        String cmd = String.format("INSERT OR REPLACE INTO `%s`(%s, %s, %s) VALUES(%s, %s, %s)",
                TABLE_NAME_CATEGORY,
                KEY_ID, KEY_SIMPLE, KEY_CATEGORY,
                DatabaseUtils.sqlEscapeString(simpleNews.news_ID),
                DatabaseUtils.sqlEscapeString(simpleNews.plain_json),
                String.valueOf(category));
        db.execSQL(cmd);
    }

    public List<SimpleNews> fetchSimple(int pageNo, int pageSize, int category) throws JSONException {
        String cmd = String.format("SELECT * FROM `%s` ORDER BY %s DESC LIMIT %s OFFSET %s",
                TABLE_NAME_CATEGORY, KEY_ID, String.valueOf(pageSize), String.valueOf(pageSize*pageNo-pageSize));
        Cursor cursor = db.rawQuery(cmd, null);
        List<SimpleNews> list = new ArrayList<SimpleNews>();
        while(cursor.moveToNext()) {
            list.add(API.GetNewsFromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_SIMPLE))), true));
        }
        cursor.close();
        return list;
    }

    public void insertDetail(DetailNews detailNews) {
        String cmd = String.format("INSERT OR REPLACE INTO `%s`(%s, %s) VALUES(%s, %s)",
                TABLE_NAME_DETAIL, KEY_ID, KEY_DETAIL,
                DatabaseUtils.sqlEscapeString(detailNews.news_ID),
                DatabaseUtils.sqlEscapeString(detailNews.plain_json));
        db.execSQL(cmd);
    }

    public DetailNews fetchDetail(String news_ID) throws JSONException {
        String cmd = String.format("SELECT * FROM `%s` WHERE %s=%s",
                TABLE_NAME_DETAIL, KEY_ID, DatabaseUtils.sqlEscapeString(news_ID));
        Cursor cursor = db.rawQuery(cmd, null);
        DetailNews detailNews = null;
        if (cursor.moveToFirst()) {
            detailNews = API.GetDetailNewsFromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_DETAIL))), true);
        }
        cursor.close();
        return detailNews;
    }

    /**
     * 下载图片，尝试3次，如果3次均不能正常下载，则返回null
     * @param url 图片链接
     * @return 图片
     */
    public Bitmap downloadImage(String url) {
        for(int times = 3; times > 0; times --) {
            try {
                URL imgUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();
                return bitmap;
            } catch(IOException e) {
                Log.d("URL_ERROR", url);
                // e.printStackTrace();
            }
        }

        return null;
    }
}
