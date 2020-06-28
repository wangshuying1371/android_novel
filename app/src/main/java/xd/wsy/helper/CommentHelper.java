package xd.wsy.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * 作者：wangshuying
 * 创建时间：2020/5/26
 * 描述：
 */
public class CommentHelper extends SQLiteOpenHelper {
    public CommentHelper(@Nullable Context context) {
        super(context, "comment.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table comment(" +
                "id integer primary key autoincrement, " +
                "username varchar(20) not null, " +
                "content varchar(50) not null, " +
                "bookName varchar(20) not null," +
                "score varchar(20) not null," +
                "comment_date Date not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
