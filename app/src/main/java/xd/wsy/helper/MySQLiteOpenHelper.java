package xd.wsy.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import xd.wsy.constants.Record;

/**
 * 作者：wangshuying
 * 创建时间：2020/5/1
 * 描述：
 */
public class MySQLiteOpenHelper {
    /*
     * 替换字符串中的单引号
     */
    private String parse(String str) {
        if (str != null) {
            if (str.contains("'"))
                return str.replaceAll("'", "''");
        }
        return str;
    }

    private SQLiteDatabase sdb;
    private final MyOpenHelper mHelper;
    private static final String DB_NAME = "local.db";   //数据库名
    private static final int DATABASE_VERSION = 3;// 数据库版本 如果更改了安装的时候会执行
    // onUpgrade方法

    public MySQLiteOpenHelper(Context context) {
        mHelper = new MyOpenHelper(context.getApplicationContext(), DB_NAME,
                null, DATABASE_VERSION);     //实例化DBOpenHelper数据库，用来创建数据库对象
        open();
    }

    private void open() {
        try {
            sdb = mHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (mHelper != null) {
            mHelper.close();
        }
        if (sdb != null) {
            sdb.close();
            sdb = null;
        }
    }

    private static final String TAG = "MyOpenSQHelper";

    /**
     * 得到自增ID 的最大值
     */
    public int getMaxId() {
        int maxId = 0;
        String sql = "select max(_id) from books";
        Cursor cursor = sdb.rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
            maxId = cursor.getInt(0);
            cursor.close();
        }
        return maxId;
    }

    /*
     * 得到数据库记录总数
     */
    public long getDataCount() {
        long count = 0l;
        Cursor cursor = sdb.rawQuery("select count(*) from books", null);
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getLong(0);
            cursor.close();
        }
        return count;
    }

    public void insertData(Record info) {
        String instr = "insert into books (bookname,author,url,icon,path) values("
                + "'"
                + parse(info.book_name)
                + "'"
                + ","
                + "'"
                + parse(info.book_author)
                + "'"
                + ","
                + "'"
                + parse(info.book_url)
                + "'"
                + ","
                + "'"
                + parse(info.book_picture_src)
                + "'"
                + ","
                + "'"
                + parse(info.book_path)
                + "'"
                + ")";
        try {
            sdb.execSQL(instr);
        } catch (Exception e) {
        }
    }

    // 查询数据
    // 搞了半天是查询的问题,在这里我犯了很低级的错误。
    public ArrayList<Record> inquiryData() {
        ArrayList<Record> list = new ArrayList<>();
        if (sdb != null) {
            Cursor cursor = sdb.query("books", null, null, null, null, null,
                    null);
            quiry(list, cursor);
        } else
            Log.e(TAG, "null SQLiteBase");


//        Log.i("TAG_listsql", String.valueOf(list));
        return list;
    }

    public void quiry(ArrayList<Record> list, Cursor cursor) {
        if (cursor == null)
            Log.e(TAG, "cursor is null");

        else if (cursor.moveToFirst()) {
            do {
                Record info = new Record(); // 注意这句不要放在do{}while之外
                String bookname = cursor.getString(cursor.getColumnIndex("bookname"));
                String author = cursor.getString(cursor.getColumnIndex("author"));    //注意这个autor
                String icon = cursor.getString(cursor.getColumnIndex("icon"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                info.book_name = bookname;
                info.book_author = author;
                info.book_picture_src = icon;
                info.book_url = url;
                info.book_path = path;
                list.add(info);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
    }

    // 删除记录
    public int deleteData(String where, String[] whereAgs) {
        return sdb.delete("books", where, whereAgs);
    }

    /*private static final class MyOpenHelper extends SQLiteOpenHelper {

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
    }*/
}
