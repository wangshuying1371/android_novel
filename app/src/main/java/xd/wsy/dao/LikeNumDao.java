package xd.wsy.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import xd.wsy.helper.LikeHelper;

/**
 * 作者：wangshuying
 * 创建时间：2020/5/26
 * 描述：
 */
public class LikeNumDao {
    private LikeHelper likeHelper;


    public LikeNumDao(Context context) {
        likeHelper = new LikeHelper(context);
    }

    public void insert(String bookName, Integer likeNum) {
        SQLiteDatabase db = likeHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("bookName", bookName);
        values.put("likeNum", likeNum);
        db.insert("likeNum", null, values);
        db.close();
    }

    public Integer queryNum(String bookName) {
        Integer likeNum = null;
        SQLiteDatabase db = likeHelper.getWritableDatabase();
        Cursor cursor = db.query("likeNum", new String[]{"likeNum"}, "bookName=?", new String[]{bookName}, null, null, null);
        while (cursor.moveToNext()) {
            likeNum = cursor.getInt(cursor.getColumnIndex("likeNum"));
        }
        if (likeNum == null) {
            likeNum = 1;
        }
//        Log.i("TAG_likeNum002", String.valueOf(likeNum));
        return likeNum;
    }

    public void updateNum(String bookName) {
        SQLiteDatabase db = likeHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Integer likeNum = queryNum(bookName);
        values.put("likeNum", likeNum + 1);
        db.update("likeNum", values, "bookName=?", new String[]{bookName});

//        Log.i("TAG_bookName_dao", bookName);
//        Log.i("TAG_likeNum_dao", String.valueOf(likeNum));
        db.close();
    }
}
