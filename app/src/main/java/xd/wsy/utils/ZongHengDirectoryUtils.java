package xd.wsy.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import xd.wsy.constants.Catalogue;


public class ZongHengDirectoryUtils {

    /**
     * 获取小说目录
     */
   /* public static List<Info> getDirectory(String url) throws InterruptedException {
        final List<Info> list = new ArrayList<Info>();
        Document doc;
        while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
            try {
                doc = Jsoup.connect(url).get();//获取网站html内容
                //运行成功则接着访问
                break;
            } catch (Exception e) {
                System.out.println("服务器过载，休息10秒！----------------------------DirectoryUtils----------------------------");
                Thread.sleep(10000);
                //结束本次循环
            }
        }
        if (doc != null) {
            Element ele = doc.getElementById("list");
            Elements es = ele.select("dt");
            ListIterator<Element> links;
            if (es.size() > 1)
                links = ele.getElementsByTag("a").listIterator(9);
            else
                links = ele.getElementsByTag("a").listIterator();
            while (links.hasNext()) {
                Element link = links.next();
                Info info = new Info();
                info.url = link.attr("abs:href");
                info.text = link.text();
                list.add(info);
            }
        }
        return list;
    }*/

    /**
     * 获取小说目录
     */
    public static List<Catalogue> getDirectory(Document doc) {
        final List<Catalogue> list = new ArrayList<Catalogue>();

        Element element = doc.getElementsByClass("all-catalog").first();
        String srcList = element.absUrl("href");

        Document docList = null;
        try {
            docList = Jsoup.connect(srcList).get();
        } catch (IOException e) {
            System.out.println("服务器过载，休息10秒！----------------------Search---------1-----------------");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        if (docList != null) {
            Element ele = docList.getElementsByClass("volume-list").first();
            Elements elements = ele.select("ul");
            for (Element element1 : elements) {
                ListIterator<Element> links;
                    links = element1.getElementsByTag("a").listIterator();
                // 获取网页所有的超链接
                while (links.hasNext()) {
                    Element link = links.next();
                    Catalogue info = new Catalogue();
                    info.chapterUrl = link.attr("abs:href");
                    info.chapterName = link.text();
                    list.add(info);
                }

            }
        }
        return list;
    }
}
