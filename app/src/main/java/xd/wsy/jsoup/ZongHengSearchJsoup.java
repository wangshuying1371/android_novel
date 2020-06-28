package xd.wsy.jsoup;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xd.wsy.constants.BiQuGeBookDetail;
import xd.wsy.constants.Catalogue;
import xd.wsy.constants.MemoryAddress;
import xd.wsy.constants.ZongHengBookDetail;
import xd.wsy.helper.SearchBiQuGeHelper;
import xd.wsy.helper.SearchZongHengHelper;
import xd.wsy.utils.ZongHengDirectoryUtils;

public class ZongHengSearchJsoup {

    private List<Catalogue> records; // 小说目录
    private Document doc = null;
    private Context mContext;

    public ZongHengSearchJsoup(Context mContext) {
        this.mContext = mContext;
    }

    public ZongHengBookDetail getBook(String url) throws IOException {
        final ZongHengBookDetail book = new ZongHengBookDetail();
        book.url = url;
//        Log.i("tag_Search_book.url", book.url);
        while (true){
            doc = null;
            try {
                doc = Jsoup.connect(url).get();
                break;
            }catch (Exception e){
                System.out.println("服务器过载，休息10秒！----------------------Search---------1-----------------");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (doc != null) {
            Element e0 = doc.getElementsByClass("book-top clearfix").first();
            book.name = e0.select("div.book-name").first().text();
            book.type  = e0.select("span").text();
            book.lastChapter = e0.select("div.book-new-chapter").text();
            book.description = e0.select("p").text();
            book.src = e0.select("img").first().absUrl("src");
            // 保存目录到本地
            saveRecords(book.name);
        }
        return book;
    }


    public void saveRecords(String key) {
        records = ZongHengDirectoryUtils.getDirectory(doc);
        final String base = MemoryAddress.saveListPath;         //saverecordpath = sdcard/read/txt

        File dir = new File(base);             //创建文件夹
        if (!dir.exists()){                   //判断目录是否存在，不存在则创建
            dir.mkdirs();            //创建目录
        }

        File file = new File(base + File.separator + key + ".txt");   //创建文件
        if (!file.exists()) {           //判断文件是否存在，不存在则创建
            try {
                if (file.createNewFile()) {      //创建文件
                    FileWriter fileWriter = new FileWriter(file);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                    for (Catalogue info : records) {
                        bufferedWriter.write(info.chapterUrl + "///" + info.chapterName);
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.close();
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取目录
     */
    public List<Catalogue> getDirectory() {
        if (records != null)
            return records;
        if (doc != null)
            return ZongHengDirectoryUtils.getDirectory(doc);
        return null;
    }

    public void destroy() {
        doc = null;
        if (records != null) {
            records.clear();
            records = null;
        }
    }

    public List<ZongHengBookDetail> search(String search, String key) throws IOException {

        List<ZongHengBookDetail> list = new ArrayList<>();

        for(int i = 0; i <= 500; i++) {
            String searchList = search + i + "/v0/s9/t0/u0/i1/ALL.html";
        while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
            doc = null;
            try {
                doc = Jsoup.connect(searchList).get();//获取网站html内容
                Log.i("TAG_searchList", searchList);

                //运行成功则接着访问
                break;
            } catch (Exception e) {
                System.out.println("服务器过载，休息10秒！-------------------------------Search------------------------------");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                //结束本次循环
            }
        }
            if (doc != null) {
                Elements es = doc.getElementsByClass("bookbox fl");   //查找div=item，这里得到一个Elements
                for (Element element : es) {           //遍历数组

                    String searchName = element.select("div.bookname").first().text();
                    String searchAuthor = element.select("div.bookilnk").select("a").get(0).text();  //查找element下首个span标签的内容
                    Element ee1 = element.select("div.bookimg").first();
                    Element ee2 = ee1.select("a").first();
                    String searchUrl = ee2.absUrl("href");
                    SearchZongHengHelper searchZongHengHelper = new SearchZongHengHelper(mContext);
                    SQLiteDatabase sqLiteDatabase = searchZongHengHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("bookName", searchName);
                    values.put("bookAuthor", searchAuthor);
                    values.put("bookUrl", searchUrl);
                    sqLiteDatabase.insert("searchZongHeng", null, values);
                    sqLiteDatabase.close();
                }
            }
        }
        return list;
    }

    public List<ZongHengBookDetail> search(String key) throws IOException  {

        List<ZongHengBookDetail> list = new ArrayList<>();
        SearchZongHengHelper searchZongHengHelper = new SearchZongHengHelper(mContext);
        SQLiteDatabase sqLiteDatabase = searchZongHengHelper.getWritableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.query(
                "searchZongHeng", new String[]{"bookName", "bookUrl"},
                null, null,null, null, null);

        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("bookName")).contains(key)) {

                ZongHengBookDetail zongHengBookDetail = new ZongHengBookDetail();
                zongHengBookDetail.name = cursor.getString(cursor.getColumnIndex("bookName"));
                list.add(zongHengBookDetail);
            }
        }
        sqLiteDatabase.close();
        return list;
    }

}
