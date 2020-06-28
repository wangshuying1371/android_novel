package xd.wsy.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import xd.wsy.common.MyApp;
import xd.wsy.constants.BaseTask;
import xd.wsy.constants.Catalogue;
import xd.wsy.service.BiQuGeSearchService;
import xd.wsy.utils.BiQuGeTextUtils;
import xd.wsy.R;
import xd.wsy.service.LoadService;
import xd.wsy.utils.LoadUtils;

public class BiQuGeListActivity extends AppCompatActivity {

    ListView listView;
    Button btnList;
    LoadService mService;
    String bookName;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((LoadService.MyBinder) service).getService();
            mService.Load(baseTask, true);
        }
    };
    List<Catalogue> infos;


    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler(){
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            loading.setVisibility(View.GONE);

            if (msg.what > 0) {

//                Log.i("TAG_infos", String.valueOf(infos));
                listView.setAdapter(new BaseAdapter() {
                    final Context c = getApplicationContext();
                    final class ViewHolder {
                        TextView textView;
                    }

                    @Override
                    public View getView(final int position, View convertView,ViewGroup parent) {
                        ViewHolder holder;
                        if (convertView == null) {
                            holder = new ViewHolder();
                            convertView = View.inflate(c, R.layout.activity_list_item, null);
                            holder.textView = convertView.findViewById(R.id.tv_list_item);
                            convertView.setTag(holder);
                        } else {
                            holder = (ViewHolder) convertView.getTag();
                        }
                        holder.textView.setText(infos.get(position).chapterName);
//                        Log.i("TAG_info.name", infos.get(position).chapterName);

                        return convertView;
                    }
                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public Object getItem(int position) {
                        return infos.get(position);
                    }

                    @Override
                    public int getCount() {
//                        Log.i("TAG_infos.size", String.valueOf(infos.size()));
                        return infos.size();
                    }
                });
            } else
                Toast.makeText(getApplicationContext(),"获取章节目录失败",0).show();
        }
    };

    final BaseTask baseTask = new BaseTask() {

        @Override
        public void run() {
//            BiQuGeLoadUtils loadUtils = new BiQuGeLoadUtils(mService,getApplicationContext());
            LoadUtils loadUtils = new LoadUtils(mService,getApplicationContext());
            MyApp app = (MyApp) getApplication();
            infos = app.getbSearchService().getDirectory();
//            Log.i("TAG_infos", String.valueOf(infos));

            // 开始下载本书
            loadUtils.loadBook(infos, bookName);
            handler.obtainMessage(infos.size()).sendToTarget();
        }
    };

    boolean mQuit = false;

    @Override
    protected void onDestroy() {
        mQuit = true;
        unbindService(connection);
        super.onDestroy();
    }

    LinearLayout loading;
    String path;

    @SuppressLint("LongLogTag")
    @Override
    protected void onStart() {
        MyApp app = (MyApp) getApplication();
        BiQuGeSearchService service = app.getbSearchService();
        service.insert(service.getBook(), bookName);
        super.onStart();
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = findViewById(R.id.lv_list);
        bookName = getIntent().getStringExtra("bookName");
        path = BiQuGeTextUtils.getTxtPath(bookName);

        btnList = findViewById(R.id.btn_list);
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(v.getContext(), BiQuGeReadActivity.class);
                intent.putExtra("bookPath", path);
                intent.putExtra("bookName", bookName);
                startActivity(intent);
            }
        });

        loading = findViewById(R.id.loading);
        Intent service = new Intent(this, LoadService.class);
        bindService(service, connection, Context.BIND_AUTO_CREATE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // 跳转到阅读器...
                Intent intent = new Intent();
                intent.setClass(view.getContext(), BiQuGeReadActivity.class);
                intent.putExtra("bookPath", path);
                intent.putExtra("bookName", bookName);
                startActivity(intent);
            }
        });
    }
}
