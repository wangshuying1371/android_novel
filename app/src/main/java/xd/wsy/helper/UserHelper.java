package xd.wsy.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * 作者：wangshuying
 * 创建时间：2020/5/11
 * 描述：
 */
public class UserHelper extends SQLiteOpenHelper {
    public UserHelper(@Nullable Context context) {
        super(context, "user.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user(" +
                "_id integer primary key autoincrement, " +
                "username varchar(20) not null, " +
                "phone integer not null, " +
                "email varchar(20) not null, " +
                "password varchar(5) not null, " +
                "passwordConfirm varchar(5) not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
