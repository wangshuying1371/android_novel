package xd.wsy.utils;

import android.annotation.SuppressLint;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import xd.wsy.constants.Catalogue;

/**
 * 作者：wangshuying
 * 创建时间：2020/4/28
 * 描述：
 */
public class Novel17DirectoryUtils {
    /**
     * 获取小说目录
     */
    @SuppressLint("LongLogTag")
    public static List<Catalogue> getDirectory(Document doc) {
        final List<Catalogue> list = new ArrayList<>();

        Element element = doc.getElementsByClass("read").select("a").first();
        String src_list = element.absUrl("href");    //目录列表

        Document docList = null;
        try {
            docList = Jsoup.connect(src_list).get();
        } catch (IOException e) {
            System.out.println("服务器过载，休息10秒！----------------------Search---------1-----------------");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        if (docList != null) {
            Element ele = docList.getElementsByClass("Main List").first();
            Elements element1 = ele.select("dd");
            ListIterator<Element> links;
            links = element1.select("a").listIterator();

            // 获取网页所有的超链接
            while (links.hasNext()) {
                Element link = links.next();
                Catalogue catalogue = new Catalogue();
                catalogue.chapterUrl = link.attr("abs:href");
                catalogue.chapterName = link.text();
                list.add(catalogue);
            }
        }
        return list;
    }

}
