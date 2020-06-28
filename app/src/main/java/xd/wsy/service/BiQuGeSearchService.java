package xd.wsy.service;

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

import xd.wsy.constants.BiQuGeBookDetail;
import xd.wsy.constants.Catalogue;
import xd.wsy.constants.Record;
import xd.wsy.jsoup.BiQuGeSearchJsoup;
import xd.wsy.utils.LocalUtils;
import xd.wsy.utils.BiQuGeTextUtils;
import xd.wsy.constants.MemoryAddress;

public class BiQuGeSearchService {

    public static final String SEARCH = "http://www.xbiquge.la/xiaoshuodaquan/";
    private boolean lock;
    private BiQuGeSearchJsoup search;
    private Context mContext;
    private BiQuGeBookDetail book;

    public BiQuGeBookDetail getBook() {
        return book;
    }

    public List<Catalogue> getDirectory() {
        return search.getDirectory();
    }

    public BiQuGeBookDetail getCache(String url) {
        if (book == null)
            return null;
        if (url.equals(book.url)) {
            return book;
        }
        return null;
    }

    public void insertBookToSQLite(String bookName, String author, String src, String url) {
        Record record = new Record();
        record.book_name = bookName;
        record.book_url = url;
        record.book_author = author;
        record.book_picture_src = src;
        record.book_path = BiQuGeTextUtils.getTxtPath(bookName);
        LocalUtils.insert(record, mContext);
    }

//    static final String TAG = BiQuGeSearchService.class.getName();

    private String getSrc(String bookName) {
        return MemoryAddress.saveIconPath + File.separator + bookName + ".png";
    }

    public void insert(BiQuGeBookDetail book, String bookName) {
        String src = getSrc(bookName);
        insertBookToSQLite(book.name, book.author, src, book.url);
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

    public byte[] getBytes(String src) {
        byte[] bytes = null;
        // 类 URL 代表一个统一资源定位符，它是指向互联网“资源”的指针。
        URL url;
        try {
            url = new URL(src);
            // 每个 HttpURLConnection 实例都可用于生成单个请求，
            // 但是其他实例可以透明地共享连接到 HTTP 服务器的基础网络
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream inputStream = conn.getInputStream();
            try {
                bytes = readInputStream(inputStream);
                if (book != null)
                    saveIcon(bytes, book.name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static byte[] readInputStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
            byteArrayOutputStream.flush();
        }
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public BiQuGeSearchService(Context c) {
        this.mContext = c;
        search = new BiQuGeSearchJsoup(mContext);
    }

    public void close() {
        if (search != null)
        {
            search.destroy();
            search = null;
        }
    }

    public void get(final String url, final Handler h) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BiQuGeBookDetail temp;
                try {
                    temp = search.getBook(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    h.obtainMessage(0).sendToTarget();
                    return;
                }
                h.obtainMessage(1, temp).sendToTarget();
                book = temp;
            }
        }).start();
    }

    /**
     *
     * @param key
     * @param h
     */
    public void search(final String key, final Handler h) {
        if (!lock) {
            lock = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<BiQuGeBookDetail> list = null;


                    /*try {
                        list = search.search(SEARCH, key);
                    } catch (IOException e) {
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
