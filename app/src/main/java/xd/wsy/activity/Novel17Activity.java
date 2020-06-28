package xd.wsy.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

import xd.wsy.R;
import xd.wsy.constants.PicData;
import xd.wsy.adapter.Novel17Adapter;
import xd.wsy.constants.Record;
import xd.wsy.jsoup.Novel17Jsoup;

public class Novel17Activity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<Record> ListFirst;
    List<Record> ListSecond;

    GridView GvFirst;
    GridView GvSecond;

    Novel17Jsoup homePageJsoup;
    EditText EtSearch;

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @SuppressLint("LongLogTag")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:{
                    Application application = getApplication();
                    GvFirst.setAdapter(new Novel17Adapter(application, ListFirst, handler));
                    GvFirst.setOnItemClickListener(Novel17Activity.this);

                    GvSecond.setAdapter(new Novel17Adapter(application, ListSecond, handler));
                    GvSecond.setOnItemClickListener(Novel17Activity.this);
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
        setContentView(R.layout.activity_novel17);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


        GvFirst = findViewById(R.id.gv_First);
        GvSecond = findViewById(R.id.gv_Second);

        homePageJsoup = new Novel17Jsoup(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ListFirst = homePageJsoup.getFirst();
                ListSecond = homePageJsoup.getSecond();
//                Record record = new Record();
//                record.count = count;
//                Log.i("TAG_count", String.valueOf(count));

                handler.obtainMessage(0).sendToTarget();
            }
        }).start();

        EtSearch = findViewById(R.id.et_search);
        EtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Novel17Activity.this, Novel17SearchActivity.class);
                startActivity(intent);

            }
        });


    }

    @SuppressLint("LongLogTag")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gv_First:
                String bookUrl1 = ListFirst.get(position).book_url;
                Intent intent1 = new Intent(view.getContext(),  Novel17DetailActivity.class);
                intent1.putExtra("bookUrl", bookUrl1);
                startActivity(intent1);
                break;
            case R.id.gv_Second:
                String bookUrl2 = ListSecond.get(position).book_url;
                Intent intent2 = new Intent(view.getContext(),  Novel17DetailActivity.class);
                intent2.putExtra("bookUrl", bookUrl2);
                startActivity(intent2);
                break;

            default:
                break;
        }
    }
}
