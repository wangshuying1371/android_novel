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
import xd.wsy.constants.Novel17BookDetail;
import xd.wsy.helper.SearchBiQuGeHelper;
import xd.wsy.helper.SearchHelper;
import xd.wsy.utils.BiQuGeDirectoryUtils;
import xd.wsy.constants.MemoryAddress;

public class BiQuGeSearchJsoup {

    private List<Catalogue> records; // 小说目录
    private Document doc = null;
    private Context mContext;

    public BiQuGeSearchJsoup(Context mContext) {
        this.mContext = mContext;
    }

    public BiQuGeBookDetail getBook(String url) throws IOException {
        final BiQuGeBookDetail book = new BiQuGeBookDetail();
        book.url = url;
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
            Element e0 = doc.getElementsByClass("box_con").first();
            Element e1 = e0.getElementById("maininfo");
            Element e2 = e1.getElementById("info");
            book.name = e2.select("h1").get(0).text();
            Elements e3 = e2.select("p");
            book.author = e3.get(0).text();
            Elements e4 = e0.getElementsByClass("con_top");
            book.type  = e4.select("a").get(2).text();
            book.lastUpdateTime = e3.get(2).text();
            book.lastChapter = e3.get(3).text();
            book.description = e1.getElementById("intro").select("p").get(1).text();
            book.src = e0.getElementById("sidebar").getElementById("fmimg").select("img").first().absUrl("src");
            Element e5 = doc.getElementsByClass("footer_link").first();
            book.recommend = e5.text();
            // 保存目录到本地
            saveRecords(book.name);
        }
        return book;
    }

    public void saveRecords(String key) {
        records = BiQuGeDirectoryUtils.getDirectory(doc);
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
            return BiQuGeDirectoryUtils.getDirectory(doc);
        return null;
    }

    public void destroy() {
        doc = null;
        if (records != null) {
            records.clear();
            records = null;
        }
    }


    public List<BiQuGeBookDetail> search(String search, String key) throws IOException {
        Log.i("TAG_search", search);
        while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
            doc = null;
            try {
                doc = Jsoup.connect(search).get();//获取网站html内容
                //运行成功则接着访问
                break;
            } catch (Exception e) {
                System.out.println("服务器过载，休息10秒！-------------------------------Search------------------------------");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        List<BiQuGeBookDetail> list = new ArrayList<>();
        if (doc != null) {
            Elements elements = doc.getElementById("main").select("li");
            for (Element element : elements) {
                Element e = element.child(0);
                String bookName = e.text();
                String bookUrl = e.absUrl("href");
                SearchBiQuGeHelper searchBiQuGeHelper = new SearchBiQuGeHelper(mContext);
                SQLiteDatabase sqLiteDatabase = searchBiQuGeHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("bookName", bookName);
                values.put("bookUrl", bookUrl);

                sqLiteDatabase.insert("searchBiQuGe", null, values);
                sqLiteDatabase.close();
            }
        }
        return list;
    }

    public List<BiQuGeBookDetail> search(String key) throws IOException {
        List<BiQuGeBookDetail> list = new ArrayList<>();
        SearchBiQuGeHelper searchBiQuGeHelper = new SearchBiQuGeHelper(mContext);
        SQLiteDatabase sqLiteDatabase = searchBiQuGeHelper.getWritableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.query(
                "searchBiQuGe", new String[]{"bookName", "bookUrl"},
                null, null,null, null, null);

        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("bookName")).contains(key)) {
                BiQuGeBookDetail biQuGeBookDetail = new BiQuGeBookDetail();
                biQuGeBookDetail.name = cursor.getString(cursor.getColumnIndex("bookName"));
                list.add(biQuGeBookDetail);
            }
        }
        sqLiteDatabase.close();
        return list;
    }
}
