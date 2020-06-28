package xd.wsy.jsoup;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xd.wsy.constants.Record;
import xd.wsy.dao.LikeNumDao;

public class ZongHengJsoup {
    static final String HOME_URL = "http://www.zongheng.com/";  //抓取的目标网址
    static final String Novel17 = "https://www.17k.com/";
    static final String ZongHeng = "http://book.zongheng.com/store/c0/c0/b0/u0/p1/v0/s9/t0/u0/i1/ALL.html/";

    Document doc = null;
    private Context context;

    public ZongHengJsoup(Context context) {
        this.context = context;
    }

    public List<Record> getHots() {
        List<Record> list = new ArrayList<>();
        while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
            doc = null;
            try {
                doc = Jsoup.connect(HOME_URL).get();//获取网站html内容
                //运行成功则接着访问
                break;
            } catch (Exception e) {
                System.out.println("服务器过载，休息10秒！---------------------------Recommend--------1-------------------");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (doc != null) {
            Elements es = doc.select("div.mind-book");   //查找div=item，这里得到一个Elements

            LikeNumDao likeNumDao = new LikeNumDao(context);
            for (Element element : es) {           //遍历数组
                Record record = new Record();       //用于存储数据
                Element e = element.select("img").first();
                record.book_picture_src = e.attr("src");
                record.book_author = element.select("div.author").first().text();  //查找element下首个span标签的内容
                Element ee1 = element.getElementsByClass("imgbox img-book fl").first();
                Element ee2 = ee1.select("a").first();
                record.book_url = ee2.absUrl("href");
                record.book_name = element.select("div.bookname").first().text();
                record.count = likeNumDao.queryNum(record.book_name);
                list.add(record);
            }
        }
        return list;
    }


    public List<Record> getMinors() {
        List<Record> list = new ArrayList<>();
        while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
            doc = null;
            try {
                doc = Jsoup.connect(ZongHeng).get();//获取网站html内容
                //运行成功则接着访问
                break;
            } catch (Exception e) {
                System.out.println("服务器过载，休息10秒！---------------------------Recommend--------1-------------------");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (doc != null) {
            Elements es = doc.getElementsByClass("bookbox fl");   //查找div=item，这里得到一个Elements

            LikeNumDao likeNumDao = new LikeNumDao(context);
            for (Element element : es) {           //遍历数组
                Record record = new Record();       //用于存储数据
                Element e = element.select("img").first();
                record.book_picture_src = e.attr("src");
                record.book_author = element.select("div.bookilnk").select("a").get(0).text();  //查找element下首个span标签的内容
                Element ee1 = element.select("div.bookimg").first();
                Element ee2 = ee1.select("a").first();
                record.book_url = ee2.absUrl("href");
                record.book_name = element.select("div.bookname").first().text();
                record.count = likeNumDao.queryNum(record.book_name);
                list.add(record);
            }
        }
        return list;
    }


    public List<Record> getCeshi() {
        List<Record> list = new ArrayList<>();
        while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
            doc = null;
            try {
                doc = Jsoup.connect("http://book.zongheng.com/store/c0/c0/b1/u0/p1/v0/s9/t0/u0/i1/ALL.html").get();//获取网站html内容
                //运行成功则接着访问
                break;
            } catch (Exception e) {
                System.out.println("服务器过载，休息10秒！---------------------------Recommend--------1-------------------");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (doc != null) {
            Elements es = doc.getElementsByClass("bookbox fl");   //查找div=item，这里得到一个Elements

            LikeNumDao likeNumDao = new LikeNumDao(context);
            for (Element element : es) {           //遍历数组
                Record record = new Record();       //用于存储数据
                Element e = element.select("img").first();
                record.book_picture_src = e.attr("src");
                record.book_author = element.select("div.bookilnk").select("a").get(0).text();  //查找element下首个span标签的内容
                Element ee1 = element.select("div.bookimg").first();
                Element ee2 = ee1.select("a").first();
                record.book_url = ee2.absUrl("href");
                record.book_name = element.select("div.bookname").first().text();
                record.count = likeNumDao.queryNum(record.book_name);
                list.add(record);
            }
        }
        return list;
    }
}
