package xd.wsy.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xd.wsy.constants.CommentList;
import xd.wsy.constants.Record;
import xd.wsy.helper.CommentHelper;

/**
 * 作者：wangshuying
 * 创建时间：2020/5/26
 * 描述：
 */
public class CommentDao {
    CommentHelper commentHelper;
    public CommentDao(Context context) {
        commentHelper = new CommentHelper(context);
    }

    private String score;

    public void pass(String score) {
        this.score = score;
    }


    public void insert(String username, String bookName, String content, Date comment_date){
        //开启数据库，准备做写入操作
        SQLiteDatabase db = commentHelper.getWritableDatabase();

        //创建ContentValues对象封装键值对
        ContentValues values = new ContentValues();

        //要插入的字段名和字段值
        values.put("username", username);
        values.put("content", content);
        values.put("bookName", bookName);
        values.put("score", score);
        values.put("comment_date", String.valueOf(comment_date));

        db.insert("comment", null, values);
        db.close();
    }

    private List<CommentList> lists = new ArrayList<>();



   public List<CommentList> queryAll(String bookName){
       SQLiteDatabase db = commentHelper.getWritableDatabase();
       Cursor cursor = db.query("comment", new String[]{"username", "content", "comment_date", "score"}, "bookName=?", new String[]{bookName}, null, null, null);

       String username = null, content, comment_date, score;
       while (cursor.moveToNext()){
           CommentList comment = new CommentList();
           username = cursor.getString(cursor.getColumnIndex("username"));
           content = cursor.getString(cursor.getColumnIndex("content"));
           comment_date = cursor.getString(cursor.getColumnIndex("comment_date"));
           score = cursor.getString(cursor.getColumnIndex("score"));

           comment.name = username;
           comment.content = content;
           comment.comment_date = comment_date;
           comment.score = score;
           lists.add(comment);

       }

       if (username == null){
           CommentList comment = new CommentList();
           comment.name = "暂无书评";

           lists.add(comment);
       }
       cursor.close();
       return lists;
   }

}
