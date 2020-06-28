package xd.wsy.jsoup;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xd.wsy.constants.Record;
import xd.wsy.dao.LikeNumDao;

public class BiQuGeJsoup {
    static final String HOME_URL = "http://www.xbiquge.la/";  //抓取的目标网址

    Document doc = null;
    private Context context;

    public BiQuGeJsoup(Context context) {
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
        LikeNumDao likeNumDao = new LikeNumDao(context);

        if (doc != null) {
            Elements es = doc.select("div.item");   //查找div=item，这里得到一个Elements
            for (Element element : es) {           //遍历数组
                Record record = new Record();       //用于存储数据
                Element e = element.select("img").first();
                record.book_picture_src = e.attr("src");
                record.book_author = element.select("span").first().text();  //查找element下首个span标签的内容
                Element e1 = element.getElementsByTag("dt").first().select("a")
                        .first();
                record.book_url = e1.absUrl("href");
                record.book_name = e1.text();
                record.count = likeNumDao.queryNum(record.book_name);
                list.add(record);
            }
        }
        return list;
    }
    public List<Record> getMinors() {
        List<Record> list = new ArrayList<>();
        if (doc == null) {
            while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
                doc = null;
                try {
                    doc = Jsoup.connect(HOME_URL).get();//获取网站html内容
                    //运行成功则接着访问
                    break;
                } catch (Exception e) {
                    System.out.println("服务器过载，休息10秒！---------------------------------------Recommend-----------2----------------");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }else {
            Elements es = doc.select("div.top");
            LikeNumDao likeNumDao = new LikeNumDao(context);
            for (Element element : es) {
                Record record = new Record();
                Element e = element.select("img").first();
                record.book_picture_src = e.attr("src");
                Element e1 = element.getElementsByTag("dt").first().select("a").first();
                record.book_url = e1.absUrl("href");
                record.book_name = e1.text();
                record.count = likeNumDao.queryNum(record.book_name);
                list.add(record);
            }
        }
        return list;
    }

    public List<Record> getList() {
        if (doc == null)

            while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
                doc = null;
                try {
                    doc = Jsoup.connect(HOME_URL).get();//获取网站html内容
                    //运行成功则接着访问
                    break;
                } catch (Exception e) {
                    System.out.println("服务器过载，休息10秒！-------------------------------Recommend-----------3------------------");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    //结束本次循环
                    continue;
                }
            }

        List<Record> list = new ArrayList<Record>();
        Element et = doc.getElementById("newscontent");
        Elements es = et.select("li");

        for (Element element : es) {
            Record record = new Record();
            Element e = element.select("a").first();
            record.book_url = e.absUrl("href");
            record.book_name = e.text();

            Elements ets = element.select("span");
            record.book_sort = ets.first().text();

            int size = ets.size();
            Element e2 = null;
            if (3 >= size) {
                e2 = ets.get(2);
            } else {
                e2 = ets.get(3);
            }
            if (e2 != null)
                record.book_author = e2.text();
            list.add(record);
        }
        return list;
    }

}
