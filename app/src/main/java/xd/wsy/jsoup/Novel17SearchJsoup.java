package xd.wsy.jsoup;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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

import xd.wsy.constants.Novel17BookDetail;
import xd.wsy.constants.Catalogue;
import xd.wsy.constants.MemoryAddress;
import xd.wsy.helper.SearchHelper;
import xd.wsy.helper.SearchNovel17Helper;
import xd.wsy.utils.Novel17DirectoryUtils;

public class Novel17SearchJsoup {

    private List<Catalogue> catalogues; // 小说目录
    private Document docDetail = null;
    private Context mContext;

    public Novel17SearchJsoup(Context mContext) {
        this.mContext = mContext;
    }

    /*获取书籍详细信息*/
    @SuppressLint("LongLogTag")
    public Novel17BookDetail getBookDetail(String url) throws IOException {
        final Novel17BookDetail bookDetail = new Novel17BookDetail();
        bookDetail.url = url;
        while (true){
            docDetail = null;
            try {
                docDetail = Jsoup.connect(url).get();
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
        if (docDetail != null) {
            Element e0 = docDetail.getElementsByClass("Info Sign").first();
            bookDetail.name = e0.select("h1").get(0).text();
            Element e1 = e0.getElementsByClass("label").first();
            bookDetail.type = e1.select("a").text();
            bookDetail.description = e0.select("p.intro").text();
            Element e2 = docDetail.getElementsByClass("NewsChapter").first();
            bookDetail.lastUpdateTime = e2.select("span").text();
            bookDetail.lastChapter = e2.select("a").get(1).text();
            bookDetail.pictureSrc = docDetail.getElementById("bookCover").select("img").first().absUrl("src");
            // 保存目录到本地
            saveRecords(bookDetail.name);
        }
        return bookDetail;
    }

    private void saveRecords(String key) throws IOException {
        catalogues = Novel17DirectoryUtils.getDirectory(docDetail);
        final String base = MemoryAddress.saveListPath;

        //创建文件夹
        File dir = new File(base);
        if (!dir.exists()){
            dir.mkdirs();
        }

        //创建文件
        File file = new File(base + File.separator + key + ".txt");   //创建文件
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                for (Catalogue catalogue : catalogues) {
                    bufferedWriter.write(catalogue.chapterUrl + "///" + catalogue.chapterName);
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取目录
     */
    public List<Catalogue> getDirectory() {
        if (catalogues != null)
            return catalogues;
        if (docDetail != null)
            return Novel17DirectoryUtils.getDirectory(docDetail);
        return null;
    }

    public void destroy() {
        docDetail = null;
        if (catalogues != null) {
            catalogues.clear();
            catalogues = null;
        }
    }

    /*在search中下载书籍*/
    @SuppressLint("LongLogTag")
    public List<Novel17BookDetail> search(String search, String key) throws IOException {

        List<Novel17BookDetail> list = new ArrayList<>();



        for(int i = 0; i <= 500; i++) {
            String searchList = search + i;
            while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
                docDetail = null;
                try {
                    docDetail = Jsoup.connect(searchList).get();//获取网站html内容
                    //运行成功则接着访问
                    break;
                } catch (Exception e) {
                    System.out.println("服务器过载--------------Search------------------------------");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            if (docDetail != null) {
                Elements elements1 = docDetail.getElementsByClass("search-list").select("div.textmiddle");
                for (Element element : elements1) {
                    Element element2 = element.select("div.textmiddle").first();
                    Elements elements3 = element2.select("dt").select("a");
                    String searchName = elements3.text();
                    String searchUrl = elements3.attr("href");
                    String searchAuthor = element2.select("span.ls").text();

                    SearchNovel17Helper searchNovel17Helper = new SearchNovel17Helper(mContext);
                    SQLiteDatabase sqLiteDatabase = searchNovel17Helper.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    values.put("bookName", searchName);
                    values.put("bookAuthor", searchAuthor);
                    values.put("bookUrl", searchUrl);
                    sqLiteDatabase.insert("searchNovel17", null, values);
                    sqLiteDatabase.close();

                }
            }
        }
        return list;
    }

    /*在search中查询书籍*/
    @SuppressLint("LongLogTag")
    public List<Novel17BookDetail> search(String key) throws IOException {

        List<Novel17BookDetail> list = new ArrayList<>();
        SearchNovel17Helper searchNovel17Helper = new SearchNovel17Helper(mContext);
        SQLiteDatabase sqLiteDatabase = searchNovel17Helper.getWritableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.query(
                "searchNovel17", new String[]{"bookName", "bookAuthor", "bookUrl"},
                null, null,null, null, null);

        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("bookName")).contains(key)) {
                Novel17BookDetail bookDetail = new Novel17BookDetail();
                bookDetail.name = cursor.getString(cursor.getColumnIndex("bookName"));
                list.add(bookDetail);
            }
        }
        sqLiteDatabase.close();
        return list;
    }

}
