package xd.wsy.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import xd.wsy.helper.UserHelper;

/**
 * 作者：wangshuying
 * 创建时间：2020/5/11
 * 描述：
 */
public class UserDao {
    private UserDao mUserDao;
    private final UserHelper user;
    public UserDao(Context context) {
        user = new UserHelper(context);
    }

    public void insert(String username, String phone, String email, String password, String passwordConfirm) {

        //开启数据库，准备做写入操作
        SQLiteDatabase db = user.getWritableDatabase();

        //创建ContentValues对象封装键值对
        ContentValues values = new ContentValues();

        //要插入的字段名和字段值
        values.put("username", username);
        values.put("phone", phone);
        values.put("email", email);
        values.put("password", password);
        values.put("passwordConfirm", passwordConfirm);

        //插入数据（表明，字段没有值时把字段维护成null， 内容值）
        db.insert("user", null, values);

        //关闭数据库
        db.close();

    }

    public boolean query(String phone, String password) {
        boolean result = false;
        SQLiteDatabase db = user.getWritableDatabase();
        Cursor cursor = db.query("user", new String[]{"phone", "password"}, null, null, null, null, null);

        //如果游标能往下移动
        while (cursor.moveToNext()) {

            //便利Cursor对象，并且跟传入username的进行比较， 如果相同就返回true，说明数据库存在该数据
            if (phone.equals(cursor.getString(0))) {
                if (password.equals(cursor.getString(1))) {
                    result = true;
                }
            }
        }

        /*一定要关闭游标，回收游标对象*/
        cursor.close();
        return result;

    }

    public String queryEmail(String phone) {
        String email = null;
        SQLiteDatabase db = user.getWritableDatabase();
        Cursor cursor = db.query("user", new String[]{"phone", "email"}, "phone" + "=?", new String[]{phone}, null, null, null);
        while (cursor.moveToNext()) {
            email = cursor.getString(cursor.getColumnIndex("email"));
        }
        return email;
    }

    public String queryName(String phone) {
        String name = null;
        SQLiteDatabase db = user.getWritableDatabase();
        Cursor cursor = db.query("user", new String[]{"username", "email"}, "phone=?", new String[]{phone}, null, null, null);
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex("username"));
        }
        return name;
    }

    public void updatePassword(String oldPassword, String newPassword) {
        SQLiteDatabase db = user.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        db.update("user", values, "password=?", new String[]{oldPassword});
        db.close();
    }


    public void delete(String phone) {
        SQLiteDatabase db = user.getWritableDatabase();
        db.delete("user", "phone=?", new String[]{phone});
    }
}
