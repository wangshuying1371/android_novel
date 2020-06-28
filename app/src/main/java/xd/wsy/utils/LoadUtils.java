package xd.wsy.utils;

import android.content.Context;
import android.util.Log;

import java.util.List;

import xd.wsy.constants.BaseTask;
import xd.wsy.constants.Catalogue;
import xd.wsy.service.LoadService;

/**
 * 作者：wangshuying
 * 创建时间：2020/5/18
 * 描述：
 */
public class LoadUtils {
    private LoadService loadService;
    private Context mContext;

    public static void insert(String key, int i, Context c) {
        c.getSharedPreferences(LOAD_TABLE, Context.MODE_PRIVATE).edit().putInt(key, i).apply();
    }

    public static int get(String key, Context c) {
        return c.getSharedPreferences(LOAD_TABLE, Context.MODE_PRIVATE).getInt(key, -1);
    }

    public LoadUtils(LoadService service, Context c) {
        this.loadService = service;
        this.mContext = c;
    }

    private static final String LOAD_TABLE = "load";

    public static final class Task extends BaseTask {

        String book;
        Context mContext;
        List<Catalogue> list;
        ZongHengTextUtils zongHengTextUtils;
        BiQuGeTextUtils biQuGeTextUtils;
        Novel17TextUtils novel17TextUtils;

        public Task(LoadService service, Context c, List<Catalogue> list, String book) {
            this.book = book;
            this.mContext = c;
            this.list = list;
            zongHengTextUtils = new ZongHengTextUtils(c);
            biQuGeTextUtils = new BiQuGeTextUtils(c);
            novel17TextUtils = new Novel17TextUtils(c);
        }

        public void load(Context c, Catalogue info, String book, int i) {
            Runnable r = info.r;
            if (r == null) {
                zongHengTextUtils.loadText(null, info, book);
                biQuGeTextUtils.loadText(null, info, book);
                novel17TextUtils.loadText(null, info, book);
                insert(book, i, c);
            }
        }

        @Override
        public void run() {
            Context c = mContext;
            int size = list.size();
            Log.i("TAG_size", String.valueOf(size));

            int index = get(book, c);
            Log.e("LoadUtils_loadBook", "index:" + index + "size:" + size);

            for (int i = index + 1; i < index + 20 && !mQuit; i++) {
                if (i < size) {
                    Log.i("TAG_index_i", String.valueOf(i));
                    Catalogue info = list.get(i);
                    load(c, info, book, i);
                }
            }
        }
    }

    public void loadBook(List<Catalogue> list, String book) {
        LoadService service = loadService;
        service.Load(new Task(service, mContext, list, book), false);
    }
}
