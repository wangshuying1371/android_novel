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
public class LikeHelper extends SQLiteOpenHelper {
    public LikeHelper(@Nullable Context context) {
        super(context, "likeNum.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table likeNum(id integer primary key autoincrement, bookName varchar(20) not null unique, likeNum integer not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
