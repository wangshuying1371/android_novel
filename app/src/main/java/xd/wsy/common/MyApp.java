package xd.wsy.common;

import android.app.Application;
import android.content.Intent;

import xd.wsy.service.BiQuGeSearchService;
import xd.wsy.service.LoadService;
import xd.wsy.service.Novel17SearchService;
import xd.wsy.service.ZongHengSearchService;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        Intent intent = new Intent(this, LoadService.class);
        startService(intent);
        super.onCreate();
    }

    BiQuGeSearchService bSearchService = new BiQuGeSearchService(this);
    public BiQuGeSearchService getbSearchService() {
        return bSearchService;
    }

    final Novel17SearchService nSearchService = new Novel17SearchService(this);
    public Novel17SearchService getnSearchService() {
        return nSearchService;
    }

    ZongHengSearchService zSearchService = new ZongHengSearchService(this);
    public ZongHengSearchService getzSearchService() {
        return zSearchService;
    }

}
