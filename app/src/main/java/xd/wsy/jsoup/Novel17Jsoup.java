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

public class Novel17Jsoup {
    private static final String Novel17 = "https://www.17k.com/";
    private Document doc = null;
    private Context context;

    public Novel17Jsoup(Context context) {
        this.context = context;
    }

    public List<Record> getFirst(){
        List<Record> list = new ArrayList<>();
        while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
            try {
                doc = Jsoup.connect(Novel17).get();//获取网站html内容
                //运行成功则接着访问
                break;
            } catch (Exception e) {
                System.out.println("服务器过载，休息10秒！");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (doc != null) {
            Element e1 = doc.getElementsByClass("QZZJ").first();
            Elements es1 = e1.select("dd");

            LikeNumDao likeNumDao = new LikeNumDao(context);

            for (Element element : es1) {
                Record homePage = new Record();
                Element e2 = element.select("img").first();
                homePage.book_picture_src = e2.attr("src");
                homePage.book_name = element.select("h3").text();
                Element e3 = element.getElementsByTag("h3").first().select("a").first();
                homePage.book_url = e3.absUrl("href");
                homePage.book_author = element.select("p.author").text();
                homePage.count = likeNumDao.queryNum(homePage.book_name);

//                Log.i("TAG_homePage.count", String.valueOf(homePage.count));
                list.add(homePage);
            }
        }
        return list;
    }
    public List<Record> getSecond(){
        List<Record> list = new ArrayList<>();
        while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
            try {
                doc = Jsoup.connect(Novel17).get();//获取网站html内容
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
            Element e1 = doc.getElementsByClass("BZZD").first();
            Elements es1 = e1.getElementsByClass("EYE");
            Elements es2 = es1.select("a");
            for (Element element : es2) {
                Record homePage = new Record();
                Element e2 = element.select("img").first();
                homePage.book_picture_src = e2.attr("src");
                homePage.book_name = element.select("span").text();
                homePage.book_url = element.absUrl("href");
                homePage.count = likeNumDao.queryNum(homePage.book_name);
                list.add(homePage);
            }
        }
        return list;
    }
}
