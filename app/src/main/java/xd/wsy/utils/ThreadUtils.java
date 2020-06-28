package xd.wsy.utils;

import xd.wsy.constants.BaseTask;
import xd.wsy.common.MyThreadPool;

public class ThreadUtils {
    private final MyThreadPool pool;

    public ThreadUtils() {
        pool = new MyThreadPool();
    }

    public void exe(BaseTask r) {
        pool.execute(r);
    }

    void shutdown() {
        pool.shutdown();
    }

    public void shut() {
        pool.shutdown();
    }

}
