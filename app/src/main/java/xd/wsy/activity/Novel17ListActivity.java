package xd.wsy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import xd.wsy.common.MyApp;
import xd.wsy.R;
import xd.wsy.constants.BaseTask;
import xd.wsy.constants.Catalogue;
import xd.wsy.service.LoadService;
import xd.wsy.service.Novel17SearchService;
import xd.wsy.utils.LoadUtils;
import xd.wsy.utils.Novel17TextUtils;

public class Novel17ListActivity extends AppCompatActivity {

    ListView LvList;
    String bookName, bookPath;
    Button BtnList;
    LoadService loadService;
    List<Catalogue> catalogues;
    LinearLayout loading;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            loadService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            loadService = ((LoadService.MyBinder) service).getService();
            loadService.Load(baseTask, true);
        }
    };

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler(){
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            loading.setVisibility(View.GONE);
            if (msg.what > 0) {
                LvList.setAdapter(new BaseAdapter() {
                    final Context context = getApplicationContext();
                    final class ViewHolder {
                        TextView textView;
                    }

                    @SuppressLint("LongLogTag")
                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent) {
                        ViewHolder holder;
                        if (convertView == null) {
                            holder = new ViewHolder();
                            convertView = View.inflate(context, R.layout.activity_list_item, null);
                            holder.textView = convertView.findViewById(R.id.tv_list_item);
                            convertView.setTag(holder);
                        } else {
                            holder = (ViewHolder) convertView.getTag();
                        }
                        holder.textView.setText(catalogues.get(position).chapterName);
                        return convertView;
                    }
                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public Object getItem(int position) {
                        return catalogues.get(position);
                    }

                    @Override
                    public int getCount() {
                        return catalogues.size();
                    }
                });
            } else
                Toast.makeText(getApplicationContext(),"获取章节目录失败",0).show();
        }
    };

    final BaseTask baseTask = new BaseTask() {
        @SuppressLint("LongLogTag")
        @Override
        public void run() {
//            Novel17LoadUtils loadUtils = new Novel17LoadUtils(loadService, getApplicationContext());
            LoadUtils loadUtils = new LoadUtils(loadService, getApplicationContext());
            MyApp app = (MyApp) getApplication();
            catalogues = app.getnSearchService().getDirectory();
            // 开始下载本书
            loadUtils.loadBook(catalogues, bookName);
            handler.obtainMessage(catalogues.size()).sendToTarget();
        }
    };

    boolean mQuit = false;

    @SuppressLint("LongLogTag")
    @Override
    protected void onStart() {
        MyApp app = (MyApp) getApplication();
        Novel17SearchService searchService = app.getnSearchService();
        searchService.insert(searchService.getBookDetail(), bookName);
        super.onStart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQuit = true;
        unbindService(connection);
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        bookName = getIntent().getStringExtra("bookName");
        bookPath = Novel17TextUtils.getTxtPath(bookName);
        loading = findViewById(R.id.loading);

        BtnList = findViewById(R.id.btn_list);
        BtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(v.getContext(), Novel17ReadActivity.class);
                intent.putExtra("bookPath", bookPath);
                intent.putExtra("bookName", bookName);
                startActivity(intent);
            }
        });

        Intent intent = new Intent(this, LoadService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        LvList = findViewById(R.id.lv_list);
        LvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // 跳转到阅读器...
                Intent intent = new Intent();
                intent.setClass(view.getContext(), Novel17ReadActivity.class);
                intent.putExtra("bookPath", bookPath);
                intent.putExtra("bookName", bookName);
                startActivity(intent);
            }
        });
    }
}
