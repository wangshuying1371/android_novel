package xd.wsy.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xd.wsy.constants.Catalogue;
import xd.wsy.constants.MemoryAddress;
import xd.wsy.constants.Record;
import xd.wsy.helper.MySQLiteOpenHelper;

public class LocalUtils {
    static final String TABLE = "index";

    /**
     * 从数据库获取本地书籍
     */
    public static List<Record> getBooks(Context context) {
        List<Record> list = null;
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context);
        list = helper.inquiryData();
        helper.close();
        return list;
    }

    /**
     * 插入数据到数据库
     */
    public static void insert(Record book, Context c) {
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(c);
        helper.insertData(book);
        helper.close();
    }

    public static void delete(Record book, Context c) {
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(c);
        helper.deleteData("bookname=?", new String[] { book.book_name});
        helper.close();
    }

    /**
     * 获取本地书籍目录
     */
    public static List<Catalogue> getRecords(String key) throws IOException {
        List<Catalogue> list = new ArrayList<>();
        File f = new File(MemoryAddress.saveListPath + "/" + key + ".txt");
        if (f.exists()) // ...
        {
            FileReader fr = null;
            try {
                fr = new FileReader(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (fr != null) // ..
            {
                BufferedReader br = new BufferedReader(fr);
                String text;

                while ((text = br.readLine()) != null) {
                    String url = text.substring(0, text.indexOf("///"));
                    String s = text.substring(text.indexOf("///") + 3);
                    Catalogue catalogue = new Catalogue();
                    catalogue.chapterUrl = url;
                    catalogue.chapterName = s;
                    list.add(catalogue);
                }
                br.close();
                fr.close();
            }
        }
        return list;
    }

}
