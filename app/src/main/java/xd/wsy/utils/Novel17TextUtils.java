package xd.wsy.utils;

import android.annotation.SuppressLint;
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

/**
 * 作者：wangshuying
 * 创建时间：2020/4/27
 * 描述：
 */
public class Novel17TextUtils {

    Context mContext;
    public Novel17TextUtils(Context c) {
        this.mContext = c;
    }

    public static final String TXT_PATH = MemoryAddress.saveBookPath + File.separator;

    public static String getTxtPath(String book) {
        return TXT_PATH + book + ".txt";
    }

    private void saveText(String title, String text, String bookName) {
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
        Log.e("TAG_TextUtils_save", "title:" + title);
        try {
            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write(text);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    public void loadText(Handler h, Catalogue catalogue, String name) {
        getText(h, catalogue, name);
    }

    @SuppressLint("LongLogTag")
    private void getText(Handler h, Catalogue catalogue, String bookName) {
        Document doc;
        String text = null;
        while (true) {// 直到服务器反应过来 再接着访问 处于过载状态 代码接着休息
            doc = null;
            try {
                doc = Jsoup.connect(catalogue.chapterUrl).get();//获取网站html内容
                //运行成功则接着访问
                break;
            } catch (Exception e) {
                System.out.println("服务器过载，休息10秒！---------------------TextUtils-----------------------------------------");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                getText(h, catalogue, bookName);
            }
        }
        if (doc != null){
            Element e = doc.getElementsByClass("p").first();

            if (e != null) {
                text = e.text();
                saveText(catalogue.chapterName, "        " +  text, bookName);
            }
        }
        if (h != null)
            h.obtainMessage(1, text).sendToTarget();
    }
}
