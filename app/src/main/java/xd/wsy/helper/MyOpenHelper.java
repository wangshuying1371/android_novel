package xd.wsy.helper;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 作者：wangshuying
 * 创建时间：2020/5/7
 * 描述：
 */
/*public class MyOpenHelper {

}*/
public class MyOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_CREATE = "create table books ("
            + "bookname  text primary key, author text not null, "
            + "icon text not null," + "url text not null,"
            + "path text not null" + ")";

    private static final String DATABASE_DELETE = "DROP TABLE IF EXISTS history";

    public MyOpenHelper(Context context, String name,
                        android.database.sqlite.SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("MyOpenSQHelperonUpgrade", "Upgrading database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");
        db.execSQL(DATABASE_DELETE);
        onCreate(db);
    }
}
