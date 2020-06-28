package xd.wsy.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import xd.wsy.adapter.BiQuGeSearchAdapter;
import xd.wsy.common.MyApp;
import xd.wsy.constants.BiQuGeBookDetail;
import xd.wsy.service.BiQuGeSearchService;
import xd.wsy.R;

public class BiQuGeSearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    EditText etSearch;
    Button btnSearch;
    BiQuGeSearchService mService;
    LinearLayout loading;
    ListView listView;
    BiQuGeSearchAdapter adapter;
    List<BiQuGeBookDetail> books;

    void hide() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        mService = ((MyApp) getApplication()).getbSearchService();
        initWidget();

        btnSearch.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View v) {
                hide();
                String key = etSearch.getText().toString();
                final Activity c = BiQuGeSearchActivity.this;
                if (!key.equals("")) {
                    loading.setVisibility(View.VISIBLE);
                    mService.search(key, new Handler() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public void handleMessage(Message msg) {
                            loading.setVisibility(View.GONE);
                            mService.unlock();
                            switch (msg.what) {
                                case 1: { // 搜索成功
                                    books = (List<BiQuGeBookDetail>) msg.obj;
                                    if (books != null) // ...
                                    {
                                        if (books.size() > 0) {
                                            adapter = new BiQuGeSearchAdapter(books, c);
                                            listView.setAdapter(adapter);
                                        }
                                    }
                                }
                                break;
                                case 0: {
                                    Toast.makeText(getApplicationContext(), "搜索失败",
                                            Toast.LENGTH_SHORT).show();
                                }
                                break;
                                default:
                                    break;
                            }
                            super.handleMessage(msg);
                        }
                    });
                } else
                    Toast.makeText(c, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
            }
        });
    }

    String bookName;

    private void initWidget() {
        loading = findViewById(R.id.loading);
        listView = findViewById(R.id.lv_list);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = books.get(position).url;
        Intent intent1 = new Intent(view.getContext(), BiQuGeDetailActivity.class);
        intent1.putExtra("bookUrl", url);
        startActivity(intent1);
    }
}
