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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyu on 2017/9/7.
 * 磁盘
 */

class FS {
    private SQLiteDatabase db;

    private static final String TABLE_NAME_SIMPLE = "news_simple";
    private static final String TABLE_NAME_DETAIL = "news_detail";
    private static final String TABLE_NAME_READ = "news_read";
    private static final String TABLE_NAME_FAVORITE = "news_favorite";
    private static final String TABLE_NAME_PICTURE = "news_picture";

    private static final String KEY_ID = "news_id"; // string
    private static final String KEY_SIMPLE = "simple_json"; // text
    private static final String KEY_CATEGORY = "category"; // integer
    private static final String KEY_DETAIL = "detail_json"; //text
    private static final String KEY_PICTURE = "picture_url"; //text

    FS(Context context) {
        this.db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().getPath() + "/data.db",null);
        // dropTables(); // FIXME
        createTables();
    }

    void createTables() {
        final String category_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string, %s integer, %s text, PRIMARY KEY(%s, %s))",
                TABLE_NAME_SIMPLE, KEY_ID, KEY_CATEGORY, KEY_SIMPLE, KEY_ID, KEY_CATEGORY);
        db.execSQL(category_table);

        final String detail_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string primary key, %s text)",
                TABLE_NAME_DETAIL, KEY_ID, KEY_DETAIL);
        db.execSQL(detail_table);

        final String read_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string primary key)",
                TABLE_NAME_READ, KEY_ID);
        db.execSQL(read_table);

        final String favorite_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string primary key)",
                TABLE_NAME_FAVORITE, KEY_ID);
        db.execSQL(favorite_table);

        final String picture_table = String.format("CREATE TABLE IF NOT EXISTS `%s`(%s string primary key, %s text)",
                TABLE_NAME_PICTURE, KEY_ID, KEY_PICTURE);
        db.execSQL(picture_table);
    }

    void dropTables() {
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_SIMPLE));
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_DETAIL));
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_READ));
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_FAVORITE));
        db.execSQL(String.format("DROP TABLE IF EXISTS `%s`", TABLE_NAME_PICTURE));
    }

    void insertSimple(SimpleNews simpleNews, int category) {
        String cmd = String.format("INSERT OR REPLACE INTO `%s`(%s, %s, %s) VALUES(%s, %s, %s)",
                TABLE_NAME_SIMPLE,
                KEY_ID, KEY_SIMPLE, KEY_CATEGORY,
                DatabaseUtils.sqlEscapeString(simpleNews.news_ID),
                DatabaseUtils.sqlEscapeString(simpleNews.plain_json),
                String.valueOf(category));
        db.execSQL(cmd);
    }

    List<SimpleNews> fetchSimple(int pageNo, int pageSize, int category) throws JSONException {
        String cmd = String.format("SELECT * FROM `%s` WHERE %s=%s ORDER BY %s DESC LIMIT %s OFFSET %s",
                TABLE_NAME_SIMPLE, KEY_CATEGORY, String.valueOf(category), KEY_ID, String.valueOf(pageSize), String.valueOf(pageSize*pageNo-pageSize));
        Cursor cursor = db.rawQuery(cmd, null);
        List<SimpleNews> list = new ArrayList<SimpleNews>();
        while(cursor.moveToNext()) {
            list.add(API.GetNewsFromJson(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_SIMPLE))), true));
        }
        cursor.close();
        return list;
    }

    void insertDetail(DetailNews detailNews) {
        String cmd = String.format("INSERT OR REPLACE INTO `%s`(%s, %s) VALUES(%s, %s)",
                TABLE_NAME_DETAIL, KEY_ID, KEY_DETAIL,
                DatabaseUtils.sqlEscapeString(detailNews.news_ID),
                DatabaseUtils.sqlEscapeString(detailNews.plain_json));
        db.execSQL(cmd);
    }

    DetailNews fetchDetail(String news_ID) throws JSONException {
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

    void insertRead(String news_ID) {
        String cmd = String.format("INSERT OR REPLACE INTO `%s`(%s) VALUES(%s)",
                TABLE_NAME_READ, KEY_ID,
                DatabaseUtils.sqlEscapeString(news_ID));
        db.execSQL(cmd);
    }

    boolean hasRead(String news_ID) {
        String cmd = String.format("SELECT * FROM `%s` WHERE %s=%s",
                TABLE_NAME_READ, KEY_ID, DatabaseUtils.sqlEscapeString(news_ID));
        Cursor cursor = db.rawQuery(cmd, null);
        boolean read = cursor.moveToFirst();
        cursor.close();
        return read;
    }

    void insertFavorite(String news_ID) {
        String cmd = String.format("INSERT OR REPLACE INTO `%s`(%s) VALUES(%s)",
                TABLE_NAME_FAVORITE, KEY_ID,
                DatabaseUtils.sqlEscapeString(news_ID));
        db.execSQL(cmd);
    }

    void removeFavorite(String news_ID) {
        String cmd = String.format("DELETE FROM `%s` WHERE %s=%s",
                TABLE_NAME_FAVORITE, KEY_ID,
                DatabaseUtils.sqlEscapeString(news_ID));
        db.execSQL(cmd);
    }

    boolean isFavorite(String news_ID) {
        String cmd = String.format("SELECT * FROM `%s` WHERE %s=%s",
                TABLE_NAME_FAVORITE, KEY_ID, DatabaseUtils.sqlEscapeString(news_ID));
        Cursor cursor = db.rawQuery(cmd, null);
        boolean favorite = cursor.moveToFirst();
        cursor.close();
        return favorite;
    }

    List<String> fetchFavorite() {
        String cmd = String.format("SELECT * FROM `%s`", TABLE_NAME_FAVORITE);
        Cursor cursor = db.rawQuery(cmd, null);
        List<String> list = new ArrayList<String>();
        while(cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex(KEY_ID)));
        }
        cursor.close();
        return list;
    }

    void insertPictureUrl(String news_ID, String url) {
        String cmd = String.format("INSERT OR REPLACE INTO `%s`(%s,%s) VALUES(%s,%s)",
                TABLE_NAME_PICTURE, KEY_ID, KEY_PICTURE,
                DatabaseUtils.sqlEscapeString(news_ID),
                DatabaseUtils.sqlEscapeString(url));
        db.execSQL(cmd);
    }

    String fetchPictureUrl(String news_ID) {
        String cmd = String.format("SELECT * FROM `%s` WHERE %s=%s",
                TABLE_NAME_PICTURE, KEY_ID, DatabaseUtils.sqlEscapeString(news_ID));
        Cursor cursor = db.rawQuery(cmd, null);
        String url = null;
        if (cursor.moveToFirst()) {
            url = cursor.getString(cursor.getColumnIndex(KEY_PICTURE));
        }
        cursor.close();
        return url;
    }

    /**
     * 下载图片，尝试3次，如果3次均不能正常下载，则返回null
     * @param url 图片链接
     * @return 图片
     */
    Bitmap downloadImage(String url) {
        for(int times = 1; times > 0; times --) {
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
