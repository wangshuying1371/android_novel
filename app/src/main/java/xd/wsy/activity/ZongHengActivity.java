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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import xd.wsy.R;
import xd.wsy.adapter.ZongHengAdapter;
import xd.wsy.constants.PicData;
import xd.wsy.constants.Record;
import xd.wsy.jsoup.ZongHengJsoup;


public class ZongHengActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<Record> hots;
    List<Record> minors;
    List<Record> ceshis;
    List<Record> lists;

    GridView hot;
    GridView minor;
    GridView ceshi;
    LinearLayout layout;

    ZongHengJsoup recommend;
    EditText editSearch;

    @SuppressLint("HandlerLeak")
    final Handler h = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
                    Application application = getApplication();
                    hot.setAdapter(new ZongHengAdapter(application, hots, h));
                    minor.setAdapter(new ZongHengAdapter(application, minors, h));
                    ceshi.setAdapter(new ZongHengAdapter(application, ceshis, h));

                    hot.setOnItemClickListener(ZongHengActivity.this);
                    minor.setOnItemClickListener(ZongHengActivity.this);
                    ceshi.setOnItemClickListener(ZongHengActivity.this);
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
        setContentView(R.layout.activity_zongheng);
        hot = findViewById(R.id.hots);
        minor =  findViewById(R.id.minor);
        ceshi =  findViewById(R.id.ceshi);
        recommend = new ZongHengJsoup(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                hots = recommend.getHots();
                minors = recommend.getMinors();
                ceshis = recommend.getCeshi();
                h.obtainMessage(0).sendToTarget();
            }
        }).start();

        editSearch = findViewById(R.id.et_search);
        editSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZongHengActivity.this, ZongHengSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        switch (parent.getId()) {
            case R.id.hots:
                String url = hots.get(position).book_url;
                Intent intent = new Intent(view.getContext(),  ZongHengDetailActivity.class);
                intent.putExtra("bookUrl", url);
                startActivity(intent);
                break;
            case R.id.minor:
                String url1 = minors.get(position).book_url;
                Intent intent1 = new Intent(view.getContext(),  ZongHengDetailActivity.class);
                intent1.putExtra("bookUrl", url1);
                startActivity(intent1);
                break;
            case R.id.ceshi:
                String url2 = ceshis.get(position).book_url;
                Intent intent2 = new Intent(view.getContext(),  ZongHengDetailActivity.class);
                intent2.putExtra("bookUrl", url2);
                startActivity(intent2);
                break;

            default:
                break;
        }
    }

}