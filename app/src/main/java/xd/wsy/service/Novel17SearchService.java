package xd.wsy.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import xd.wsy.constants.Novel17BookDetail;
import xd.wsy.constants.Catalogue;
import xd.wsy.constants.MemoryAddress;
import xd.wsy.constants.Record;
import xd.wsy.jsoup.Novel17SearchJsoup;
import xd.wsy.utils.Novel17TextUtils;
import xd.wsy.utils.LocalUtils;

/**
 * 作者：wangshuying
 * 创建时间：2020/4/25
 * 描述：
 */
public class Novel17SearchService {


    private static final String SEARCH = "https://search.17k.com/search.xhtml?c.q=&c.st=0&book_status=0&word_count=0&book_free=0&sort=0&fuzzySearchType=1&page=";
    private Novel17BookDetail bookDetail;
    private Context mContext;
    private Novel17SearchJsoup search;
    private boolean lock;

    public Novel17BookDetail getBookDetail() {
        return bookDetail;
    }

    public List<Catalogue> getDirectory() {
        return search.getDirectory();
    }

    @SuppressLint("LongLogTag")
    public Novel17SearchService(Context c) {
        this.mContext = c;
        search = new Novel17SearchJsoup(mContext);
    }

    @SuppressLint("LongLogTag")
    public Novel17BookDetail getCache(String url) {
        if (bookDetail == null) {
            return null;
        }
        if (url.equals(bookDetail.url)) {
            return bookDetail;
        }
        return null;
    }

    public void get(final String url, final Handler h) {
        new Thread(new Runnable() {
            @SuppressLint("LongLogTag")
            @Override
            public void run() {
                Novel17BookDetail temp;
                try {
                    temp = search.getBookDetail(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    h.obtainMessage(0).sendToTarget();
                    return;
                }
                h.obtainMessage(1, temp).sendToTarget();
                bookDetail = temp;
            }
        }).start();
    }

    public void saveIcon(byte[] bytes, String bookName) {

        File dir = new File(MemoryAddress.saveIconPath);
        if (!dir.exists())
            dir.mkdirs();

        // 本地地址
        String src = getSrc(bookName);

        // 存到本地
        File file = new File(src);
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.close();     //关闭输出流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getSrc(String bookName) {
        return MemoryAddress.saveIconPath + File.separator + bookName + ".png";
    }

    public byte[] getBytes(String src) {
        byte[] bytes = null;
        // 类 URL 代表一个统一资源定位符，它是指向互联网“资源”的指针。
        URL url;
        try {
            url = new URL(src);
//            url = new URL("http:" + src);

            // 每个 HttpURLConnection 实例都可用于生成单个请求，
            // 但是其他实例可以透明地共享连接到 HTTP 服务器的基础网络
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream inputStream = conn.getInputStream();
            try {
                bytes = readInputStream(inputStream);
                if (bookDetail != null)
                    saveIcon(bytes, bookDetail.name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private static byte[] readInputStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
            byteArrayOutputStream.flush();
        }
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public void insert(Novel17BookDetail book, String bookName) {
        String pictureSrc = getSrc(bookName);
        insertBookToSQLite(book.name, pictureSrc, book.url);
    }

    @SuppressLint("LongLogTag")
    public void insertBookToSQLite(String bookName, String src, String url) {
        Record record = new Record();
        record.book_name = bookName;
        record.book_url = url;
        record.book_picture_src = src;
        record.book_path = Novel17TextUtils.getTxtPath(bookName);
        LocalUtils.insert(record, mContext);
    }



    public void search(final String key, final Handler h) {
        if (!lock) {
            lock = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Novel17BookDetail> list = null;

                    /*try {
                        list = search.search(SEARCH, key);
                    } catch (IOException e) {
                        h.obtainMessage(0).sendToTarget();
                        e.printStackTrace();
                    }*/
                    try {
                        list = search.search(key);
                    } catch (IOException e) {
                        e.printStackTrace();
                        h.obtainMessage(0).sendToTarget();
                    }


                    h.obtainMessage(1, list).sendToTarget();
                }
            }).start();
        }
    }

    public void unlock() {
        lock = false;
    }
}
