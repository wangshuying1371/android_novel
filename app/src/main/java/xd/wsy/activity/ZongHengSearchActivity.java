package xd.wsy.activity;

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

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import xd.wsy.R;
import xd.wsy.adapter.ZongHengSearchAdapter;
import xd.wsy.common.MyApp;
import xd.wsy.constants.ZongHengBookDetail;
import xd.wsy.service.ZongHengSearchService;

public class ZongHengSearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    EditText EtSearch;
    Button BtnSearch;
    ZongHengSearchService searchService;
    LinearLayout loading;
    ListView listView;
    ZongHengSearchAdapter searchAdapter;
    List<ZongHengBookDetail> bookDetails;



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
        searchService = ((MyApp) getApplication()).getzSearchService();
        initWidget();

        BtnSearch.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View v) {
                hide();
                String key = EtSearch.getText().toString();
                final Activity c = ZongHengSearchActivity.this;
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
                                    bookDetails = (List<ZongHengBookDetail>) msg.obj;
                                    if (bookDetails != null) // ...
                                    {
                                        if (bookDetails.size() > 0) {
                                            searchAdapter = new ZongHengSearchAdapter(bookDetails, c);
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









        //////////////////////////////////////////////////////////////////

        /*SQLiteDatabase db;
        Cursor cursor;
        SimpleCursorAdapter cursorAdapter;

        //创建数据库
        SearchHelper searchHelper = new SearchHelper(this);
        db = searchHelper.getWritableDatabase();

        cursor = db.rawQuery("select * from search", null);
        cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, new String[]{"name", "age"}, new int[]{android.R.id.text1, android.R.id.text2}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
*/

        ///////////////////////////////////////////////////////////////////




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
        Intent intent = new Intent(view.getContext(), ZongHengDetailActivity.class);
        intent.putExtra("bookUrl", "https:" + url);
        startActivity(intent);
    }
}
