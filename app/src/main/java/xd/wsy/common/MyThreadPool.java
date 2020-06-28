package xd.wsy.common;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import xd.wsy.constants.BaseTask;

public class MyThreadPool implements Runnable{

    private final Thread mThread;
    private Handler mHandler; // 消息处理
    private boolean mQuit = false; // 是否退出
    private List<BaseTask> tasks;

    static final String TAG = "MyThreadPool";

    public MyThreadPool() {
        mThread = new Thread(this);
        mThread.start();
        tasks = new ArrayList<>();
    }

    /**
     * UI线程
     */
    public void removeAll() {
        if (mHandler != null)
            mHandler.removeMessages(1);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        BaseTask r = (BaseTask) msg.obj;
                        if (r!= null)
                            r.run();
                        break;
                    default:
                        break;
                }
            }
        };
        Looper.loop();
    }

    /**
     * UI线程 添加任务
     */
    public void execute(BaseTask r) {
        if (!mQuit) {
            if (mHandler != null) {
                if (!mHandler.hasMessages(1, r)) {
                    mHandler.obtainMessage(1, r).sendToTarget();
                    tasks.add(r);
                } else
                    Log.w(TAG, "hasMessage");
            } else
                Log.e(TAG, "Handler = null");
        } else
            Log.e(TAG, "have Quit");
    }

    private void clear() {
        for (int i = 0; i < tasks.size(); i++)
            tasks.remove(i).mQuit = true;
    }

    public void shutdown() {
        if (!mQuit) {
            mQuit = true;
            if (mThread != null) {
                if (mHandler != null) {
                    mHandler.getLooper().quit();
                    mHandler.removeMessages(1);
                    clear();
                }
                if (Thread.currentThread() != mThread) {
                    if (mThread.isAlive())
                        try {
                            mThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }
            }
        }
    }
}
