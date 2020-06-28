package xd.wsy.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import xd.wsy.constants.Catalogue;
import xd.wsy.constants.MemoryAddress;

public class ZongHengTextUtils {
    Context mContext;
    public ZongHengTextUtils(Context c) {
        this.mContext = c;
    }
    public static final String TXT_PATH = MemoryAddress.saveBookPath + File.separator;

    public static String getTxtPath(String book) {
        return TXT_PATH + book + ".txt";
    }

    private void save(String title, String text, String bookName) {
       File dir = new File(TXT_PATH);
       if (!dir.exists()){
           dir.mkdirs();
       }
       File file = new File(getTxtPath(bookName));
       if (!file.exists()){                  //如果文件不存在
           try {
               file.createNewFile();         //创建文件
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
        Log.e("TextUtils_save", "title:" + title);
        try {
            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write(text);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadText(Handler h, Catalogue info, String name) {
        getText(h, info, name);
    }

    private void getText(Handler h, Catalogue info, String book) {
        Document doc;
        String text = null;
        while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
            doc = null;
            try {
                doc = Jsoup.connect(info.chapterUrl).get();//获取网站html内容
                //运行成功则接着访问
                break;
            } catch (Exception e) {
                System.out.println("服务器过载，休息10秒！---------------------TextUtils-----------------------------------------");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                getText(h, info, book);
            }
        }
        if (doc != null){
            Element e = doc.getElementsByClass("content").first();
            if (e != null) {
                text = e.text();
                save(info.chapterName, "        " +  text, book);
            }
        }
        if (h != null)
            h.obtainMessage(1, text).sendToTarget();
    }

}
