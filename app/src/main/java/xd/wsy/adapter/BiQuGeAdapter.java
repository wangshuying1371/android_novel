package xd.wsy.adapter;

import android.app.Application;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xd.wsy.common.MyApp;
import xd.wsy.constants.PicData;
import xd.wsy.R;
import xd.wsy.constants.Record;

public class BiQuGeAdapter extends BaseAdapter {

    private Application mApplication;
    private List<Record> hots;
    private Handler h;

    public BiQuGeAdapter(Application a, List<Record> list, Handler h) {
        mApplication = a;
        hots = list;
        this.h = h;
    }

    static class ViewHolder {
        ImageView iv;
        TextView name, author, TvCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vHolder;
        if (convertView == null) {
            vHolder = new ViewHolder();
            convertView = View.inflate(mApplication, R.layout.activity_biquge_hots_item, null);
            vHolder.iv = convertView.findViewById(R.id.icon);
            vHolder.name = convertView.findViewById(R.id.tv_searchName);
//            vHolder.author = convertView.findViewById(R.id.author);
            vHolder.TvCount = convertView.findViewById(R.id.count);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }
        final Record r = hots.get(position);
        vHolder.name.setText(r.book_name);
        vHolder.TvCount.setText(String.valueOf(r.count));
        /*if (r.author != null)
            vHolder.author.setText(r.author);*/
        if (!r.IsShow) {
            r.IsShow = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PicData pic = new PicData(vHolder.iv);
                    MyApp app = (MyApp) mApplication;
                    pic.bytes = app.getbSearchService().getBytes(r.book_picture_src);

//                    Log.e("TAG_firstAdapter", r.src);

                    h.obtainMessage(1, pic).sendToTarget();
                }
            }).start();
        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return hots.get(position);
    }

    @Override
    public int getCount() {
        return hots.size();
    }
}
