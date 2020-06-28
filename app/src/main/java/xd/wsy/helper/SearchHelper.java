package xd.wsy.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * 作者：wangshuying
 * 创建时间：2020/5/6
 * 描述：
 */
public class SearchHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "search.db";
    private static final int DATABASE_VERSION = 1;

    public SearchHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table search(bookName text primary key, bookAuthor text, bookUrl text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }





}
