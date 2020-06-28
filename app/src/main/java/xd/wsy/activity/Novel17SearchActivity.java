package xd.wsy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import xd.wsy.common.MyApp;
import xd.wsy.R;
import xd.wsy.adapter.Novel17SearchAdapter;
import xd.wsy.constants.Novel17BookDetail;
import xd.wsy.service.Novel17SearchService;

public class Novel17SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    EditText EtSearch;
    Button BtnSearch;
    Novel17SearchService searchService;
    LinearLayout loading;
    ListView listView;
    Novel17SearchAdapter searchAdapter;
    List<Novel17BookDetail> bookDetails;



    void hide() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        EtSearch = findViewById(R.id.et_search);
        BtnSearch = findViewById(R.id.btn_search);
        searchService = ((MyApp) getApplication()).getnSearchService();
        initWidget();

        BtnSearch.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View v) {
                hide();
                String key = EtSearch.getText().toString();
                final Activity c = Novel17SearchActivity.this;
                if (!key.equals("")) {
                    loading.setVisibility(View.VISIBLE);
                    searchService.search(key, new Handler() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public void handleMessage(Message msg) {
                            loading.setVisibility(View.GONE);
                            searchService.unlock();
                            switch (msg.what) {
                                case 1: { // 搜索成功
                                    bookDetails = (List<Novel17BookDetail>) msg.obj;
                                    Log.i("TAG_bookDetails", String.valueOf(bookDetails));
                                    if (bookDetails != null) // ...
                                    {
                                        if (bookDetails.size() > 0) {
                                            searchAdapter = new Novel17SearchAdapter(bookDetails, c);
                                            listView.setAdapter(searchAdapter);
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

    private void initWidget() {
        loading = findViewById(R.id.loading);
        listView = findViewById(R.id.lv_list);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = bookDetails.get(position).url;
        Log.i("TAG_SearchActivity_url", url);
        Intent intent = new Intent(view.getContext(), Novel17DetailActivity.class);
        intent.putExtra("bookUrl", "https:" + url);
        startActivity(intent);
    }
}
