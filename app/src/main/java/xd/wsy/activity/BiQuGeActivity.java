package xd.wsy.activity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import xd.wsy.R;
import xd.wsy.adapter.BiQuGeAdapter;
import xd.wsy.common.MyItemTouchListener;
import xd.wsy.constants.PicData;
import xd.wsy.constants.Record;
import xd.wsy.jsoup.BiQuGeJsoup;

public class BiQuGeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<Record> hots;
    List<Record> minors;
    List<Record> lists;

    GridView hot;
    GridView minor;
    LinearLayout layout;

    BiQuGeJsoup recommend;
    EditText editSearch;

    @SuppressLint("HandlerLeak")
    final Handler h = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
                    Application application = getApplication();
                    hot.setAdapter(new BiQuGeAdapter(application, hots, h));
                    minor.setAdapter(new BiQuGeAdapter(application, minors, h));

                    hot.setOnItemClickListener(BiQuGeActivity.this);
                    minor.setOnItemClickListener(BiQuGeActivity.this);
                    initList();
                }
                break;
                case 1:
                    PicData data = (PicData) msg.obj;
                    if (data != null ) {
                        Bitmap bitmap;
                        byte[] bytes = data.bytes;
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        data.iv.setImageBitmap(bitmap);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biquge);
        hot = findViewById(R.id.hots);
        minor =  findViewById(R.id.minor);
        layout =  findViewById(R.id.lv_list);
        recommend = new BiQuGeJsoup(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                hots = recommend.getHots();
                minors = recommend.getMinors();
                lists = recommend.getList();
                h.obtainMessage(0).sendToTarget();
            }
        }).start();

        editSearch = findViewById(R.id.et_search);
        editSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BiQuGeActivity.this, BiQuGeSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        switch (parent.getId()) {
            case R.id.hots:
                String url = hots.get(position).book_url;
                Intent intent = new Intent(view.getContext(),  BiQuGeDetailActivity.class);
                intent.putExtra("bookUrl", url);
                startActivity(intent);
                break;
            case R.id.minor:
                String url1 = minors.get(position).book_url;
                Intent intent1 = new Intent(view.getContext(),  BiQuGeDetailActivity.class);
                intent1.putExtra("bookUrl", url1);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    void initList() {
//        MyItemTouchListener listener = new MyItemTouchListener(this);
        List<Record> list = lists;
        int size = list.size();
        MyClick myClick = new MyClick();
        for (int i = 0; i < size; i++) {
            Record r = list.get(i);
            View v = View.inflate(this, R.layout.activity_biquge_list_item, null);
            TextView sort = v.findViewById(R.id.sort);
            TextView name = v.findViewById(R.id.tv_searchName);
            TextView author = v.findViewById(R.id.tv_searchAuthor);
            sort.setText(r.book_sort);
            name.setText(r.book_name);
            author.setText(r.book_author);
            v.setTag(r.book_url);
            v.setOnClickListener(myClick);
//            v.setOnTouchListener(listener);
            layout.addView(v);
        }
    }

    public final class MyClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.frist_list) {
                String url = (String) v.getTag();
                Intent intent = new Intent(v.getContext(), BiQuGeDetailActivity.class);
                intent.putExtra("bookUrl", url);
                startActivity(intent);
            }
        }
    }
}