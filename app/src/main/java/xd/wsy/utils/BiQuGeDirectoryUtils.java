package xd.wsy.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import xd.wsy.constants.Catalogue;
public class BiQuGeDirectoryUtils {

    /**
     * 获取小说目录
     */
    public static List<Catalogue> getDirectory(String url) throws InterruptedException {
        final List<Catalogue> list = new ArrayList<Catalogue>();
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
                Catalogue info = new Catalogue();
                info.chapterUrl = link.attr("abs:href");
                info.chapterName = link.text();
                list.add(info);
            }
        }
        return list;
    }

    /**
     * 获取小说目录
     */
    public static List<Catalogue> getDirectory(Document doc) {
        final List<Catalogue> list = new ArrayList<Catalogue>();
        if (doc != null) {
            Element ele = doc.getElementById("list");
            ListIterator<Element> links;
            Elements es = ele.select("dl");
            if (es.size() > 1)
                links = ele.getElementsByTag("a").listIterator(9);
            else
                links = ele.getElementsByTag("a").listIterator();
            // 获取网页所有的超链接
            // Elements links = doc.select("a[href]");
            while (links.hasNext()) {
                Element link = links.next();
                Catalogue info = new Catalogue();
                info.chapterUrl = link.attr("abs:href");
                info.chapterName = link.text();
                list.add(info);
            }
        }
        return list;
    }
}
