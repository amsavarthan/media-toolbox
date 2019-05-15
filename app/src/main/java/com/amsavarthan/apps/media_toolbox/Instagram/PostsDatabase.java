package com.amsavarthan.apps.media_toolbox.Instagram;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by amsavarthan on 2/3/18.
 */

public class PostsDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "posts.db";
    public static final String POST_TABLE_NAME = "post";
    public static final String POST_COLUMN_ID = "id";
    public static final String POST_COLUMN_USERNAME = "username";
    public static final String POST_COLUMN_FULL_NAME = "full_name";
    public static final String POST_COLUMN_IS_VIDEO = "is_video";
    public static final String POST_COLUMN_DISPLAY_URL = "display_url";
    public static final String POST_COLUMN_LINK = "link";
    public static final String POST_COLUMN_THUMBNAIL = "thumbnail";
    public static final String POST_COLUMN_CAPTION = "caption";
    public static final String POST_COLUMN_VDO_URL = "video_url";
    public static final String POST_COLUMN_TIME = "timestamp";
    public static final String POST_COLUMN_PIC_URL = "profile_pic_url";
    private HashMap hp;

    public PostsDatabase(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table post " + "(id integer primary key," +
                        "username text," +
                        "full_name text," +
                        "is_video text," +
                        "display_url text," +
                        "link text," +
                        "profile_pic_url text," +
                        "video_url text," +
                        "timestamp text," +
                        "thumbnail text," +
                        "caption text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS post");
        onCreate(db);
    }

    public void insertContact(String username,String full_name,String display_url,String is_video,String link,String profile_pic_url,String video_url,String timestamp,String thumbnail,String caption) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("full_name", full_name);
        contentValues.put("display_url", display_url);
        contentValues.put("is_video", is_video);
        contentValues.put("link", link);
        contentValues.put("profile_pic_url", profile_pic_url);
        contentValues.put("video_url", video_url);
        contentValues.put("timestamp", timestamp);
        contentValues.put("thumbnail", thumbnail);
        contentValues.put("caption", caption);
        db.insert("post", null, contentValues);
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from post where id="+id+"", null );
    }

    public int getCount(){

        String countQuery="SELECT * FROM "+POST_TABLE_NAME;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery,null);

        int count=cursor.getCount();
        cursor.close();

        return count;

    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("post",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public void deleteAll () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("post",
                null,
                null);
    }

}