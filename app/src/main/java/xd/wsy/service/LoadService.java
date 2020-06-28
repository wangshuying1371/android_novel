package xd.wsy.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import xd.wsy.constants.BaseTask;
import xd.wsy.utils.ThreadUtils;

public class LoadService extends Service {

   public LoadService() {
    }

    private ThreadUtils threadUtils;
    private MyBinder mybinder;
    private int bindCount;

    public final class MyBinder extends Binder {

        public LoadService getService() {
            return LoadService.this;
        }
    }

    static final String TAG = "LoadService";

    @Override
    public IBinder onBind(Intent intent) {
        bindCount++;
        Log.v(TAG, "服务已连接");
        return mybinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bindCount--;
        if (bindCount == 0) {
            stopSelf();
            Log.v(TAG, "服务销毁");
        }
        return super.onUnbind(intent);
    }

    public void Load(BaseTask r, boolean isRemollc) {
        if (isRemollc)
            Log.i("TAG_LoadService", "remollc");
        threadUtils.exe(r);
    }

    @Override
    public void onCreate() {
        threadUtils = new ThreadUtils();
        mybinder = new MyBinder();
        super.onCreate();
        Log.v(TAG, "服务已创建");
    }

    @Override
    public void onDestroy() {
        threadUtils.shut();
        super.onDestroy();
        Log.v(TAG, "服务已停止");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "服务已启动");
        return super.onStartCommand(intent, flags, startId);
    }
}
