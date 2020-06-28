package xd.wsy.adapter;

import android.app.Application;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xd.wsy.common.MyApp;
import xd.wsy.R;

import xd.wsy.constants.PicData;
import xd.wsy.constants.Record;

public class Novel17Adapter extends BaseAdapter {

    private Application mApplication;
    private List<Record> mRecordList;
    private Handler mHandler;

    public Novel17Adapter(Application a, List<Record> list, Handler h ) {
        mApplication = a;
        mRecordList = list;
        this.mHandler = h;
    }

    static class ViewHolder {
        ImageView IvBookPicture;
        TextView TvBookName, TvBookAuthor, TvCount;
    }

    @Override
    public int getCount() {
        return mRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vHolder;
        if (convertView == null) {
            vHolder = new ViewHolder();
            convertView = View.inflate(mApplication, R.layout.activity_novel17_item, null);
            vHolder.IvBookPicture = convertView.findViewById(R.id.iv_bookPicture);
            vHolder.TvBookName = convertView.findViewById(R.id.tv_bookName);

            vHolder.TvCount = convertView.findViewById(R.id.count);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }
        final Record homePage = mRecordList.get(position);
        vHolder.TvBookName.setText(homePage.book_name);
        vHolder.TvCount.setText(String.valueOf(homePage.count));
        if (!homePage.IsShow) {
            homePage.IsShow = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PicData mPicData = new PicData(vHolder.IvBookPicture);
                    MyApp app = (MyApp) mApplication;
                    mPicData.bytes = app.getnSearchService().getBytes("https:" + homePage.book_picture_src);

                    mHandler.obtainMessage(1, mPicData).sendToTarget();
                }
            }).start();

        }
        return convertView;
    }
}