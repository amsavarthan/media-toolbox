package com.amsavarthan.apps.media_toolbox.Youtube;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by amsavarthan on 2/3/18.
 */

public class VideosDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "videos.db";
    public static final String VIDEO_TABLE_NAME = "videos";
    public static final String VIDEO_COLUMN_ID = "id";
    public static final String VIDEO_COLUMN_URL = "url";
    public static final String VIDEO_COLUMN_THUMBNAIL_URL = "thumbnail";
    public static final String VIDEO_COLUMN_THUMBNAIL_MAX_URL = "thumbnail_max";
    public static final String VIDEO_COLUMN_TITLE = "title";

    private HashMap hp;

    public VideosDatabase(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table videos " + "(id integer primary key autoincrement," +
                        "url text," +
                        "thumbnail text," +
                        "thumbnail_max text," +
                        "title text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS videos");
        onCreate(db);
    }

    public void insertVideo(String url,String thumbnail,String title,String thumbnail_max) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("url", url);
        contentValues.put("thumbnail", thumbnail);
        contentValues.put("thumbnail_max", thumbnail_max);
        contentValues.put("title", title);
        db.insert("videos", null, contentValues);
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from videos where id="+id+"", null );
    }

    public int getCount(){

        String countQuery="SELECT * FROM "+VIDEO_TABLE_NAME;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery,null);

        int count=cursor.getCount();
        cursor.close();

        return count;

    }

    public Integer deleteVideo (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("videos",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public void deleteAll () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("videos",
                null,
                null);
    }

}