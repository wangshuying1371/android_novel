package xd.wsy.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * 作者：wangshuying
 * 创建时间：2020/5/12
 * 描述：
 */
public class SearchZongHengHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "searchZongHeng.db";
    private static final int DATABASE_VERSION = 1;

    public SearchZongHengHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table searchZongHeng(bookName text primary key, bookAuthor text, bookUrl text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
