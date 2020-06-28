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
public class SearchBiQuGeHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "searchBiQuGe.db";
    private static final int DATABASE_VERSION = 1;

    public SearchBiQuGeHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table searchBiQuGe(bookName text primary key, bookUrl text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
