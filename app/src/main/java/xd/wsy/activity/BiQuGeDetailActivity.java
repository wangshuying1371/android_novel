package xd.wsy.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.List;

import xd.wsy.R;
import xd.wsy.common.MyApp;
import xd.wsy.constants.BiQuGeBookDetail;
import xd.wsy.constants.CommentList;
import xd.wsy.constants.Record;
import xd.wsy.dao.CommentDao;
import xd.wsy.dao.LikeNumDao;
import xd.wsy.dao.UserDao;
import xd.wsy.service.BiQuGeSearchService;
import xd.wsy.utils.BiQuGeTextUtils;

public class BiQuGeDetailActivity extends AppCompatActivity {

    ImageView ivDetail;
    TextView name, author,  type, lastUpdate, lastChapter, recommend, description;
    RelativeLayout bookLayout, RlMenu;
    LinearLayout loading;
    Button btnRead;
    String bookName;
    private SharedPreferences sp;
    private SharedPreferences sp1;
    private SharedPreferences.Editor editor1;
    int count;
    Boolean isLike;

    @SuppressLint("HandlerLeak")
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biquge_detail);
        initWidget();
        String url = getIntent().getStringExtra("bookUrl");
        MyApp app = (MyApp) getApplication();
        final BiQuGeSearchService mService = app.getbSearchService();
        if (url != null && !url.equals("")) {
            loading.setVisibility(View.VISIBLE);
            BiQuGeBookDetail book = mService.getCache(url);
            if (book != null) {
                loading.setVisibility(View.GONE);
                showBookDetail(book, mService);
            } else {
                mService.get(url, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        loading.setVisibility(View.GONE);
                        switch (msg.what) {
                            case 1:
                                BiQuGeBookDetail book = (BiQuGeBookDetail) msg.obj;
                                if (book != null)
                                    showBookDetail(book, mService);
                                break;
                            case 0:
                                Toast.makeText(getApplicationContext(), "加载失败",  Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private void showBookDetail(final BiQuGeBookDetail book, final BiQuGeSearchService mService) {
        bookLayout.setVisibility(View.VISIBLE);
        final Handler h = new Handler() {
            public void handleMessage(Message msg) {
                Bitmap bm = (Bitmap) msg.obj;
                if (bm != null)
                    ivDetail.setImageBitmap(bm);
            }
        };
        new Thread(new Runnable() {

            @Override
            public void run() {
                byte[] bytes = mService.getBytes(book.src);
                if (bytes != null) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    h.obtainMessage(1, bm).sendToTarget();
                }
            }
        }).start();
        bookName = book.name;
        name.setText(book.name);
        author.setText(book.author);
        type.setText(book.type);
        lastUpdate.setText(book.lastUpdateTime);
        lastChapter.setText(book.lastChapter);
        recommend.setText(book.recommend);
        description.setText("   " + book.description);




        final TextView TvWriteComment = findViewById(R.id.tv_writeCommend);
        final View view = getLayoutInflater().inflate(R.layout.pop_commend, null);
        final PopupWindow popupWindow = new PopupWindow(view, 1000, 600);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        TvWriteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAtLocation(TvWriteComment, Gravity.CENTER, 0, 200);
                /**
                 * 点击popupWindow让背景变暗
                 */
                final WindowManager.LayoutParams lp = BiQuGeDetailActivity.this.getWindow().getAttributes();
                lp.alpha = 0.3f;//代表透明程度，范围为0 - 1.0f
                BiQuGeDetailActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                BiQuGeDetailActivity.this.getWindow().setAttributes(lp);
            }
        });
        /**
         * 退出popupWindow时取消暗背景
         */
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                final WindowManager.LayoutParams lp = BiQuGeDetailActivity.this.getWindow().getAttributes();
                lp.alpha = 1.0f;
                BiQuGeDetailActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                BiQuGeDetailActivity.this.getWindow().setAttributes(lp);
            }
        });

        final EditText EtWrite = view.findViewById(R.id.et_write);
        Button BtnFinish = view.findViewById(R.id.btn_finish);


        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        final String phone = sp.getString("phone", String.valueOf(0));
        final CommentDao commentDao = new CommentDao(getApplicationContext());
        UserDao userDao = new UserDao(getApplicationContext());
        final String username = userDao.queryName(phone);

        final RatingBar ratingBar_default = view.findViewById(R.id.ratingbar_default);
        ratingBar_default.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                TextView TvScore = view.findViewById(R.id.tv_score);
                Log.i("TAG___________", "hahahaha");
                Log.i("TAG_rating", String.valueOf(rating));
                TvScore.setText("书籍评分" + rating);
                commentDao.pass("书籍评分" + rating);

            }});

        BtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String textWrite = EtWrite.getText().toString();
                Log.i("TAG_textWrite", textWrite);
                final Date t = new Date();
                Log.i("TAG_t", String.valueOf(t));
                Log.i("TAG_username", username);
                commentDao.insert(username, bookName, textWrite, t);

                popupWindow.dismiss();
            }
        });

        final List<CommentList> text111 = commentDao.queryAll(bookName);
        ListView listView = findViewById(R.id.lv_comment);
        listView.setAdapter(new BaseAdapter() {
            final Context c = getApplicationContext();
            final class ViewHolder{
                TextView TvName;
                TextView TvContent;
                TextView commentDate;
                TextView score;
            }
            @Override
            public int getCount() {
                return text111.size();
            }

            @Override
            public Object getItem(int position) {
                return text111.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = View.inflate(c, R.layout.comment_list, null);
                    holder.TvName = convertView.findViewById(R.id.commentName);
                    holder.TvContent = convertView.findViewById(R.id.content);
                    holder.commentDate = convertView.findViewById(R.id.tv_date);
                    holder.score = convertView.findViewById(R.id.tv_scoreList);
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                CommentList commentList = text111.get(position);
                holder.TvName.setText(commentList.name);
                holder.TvContent.setText(commentList.content);
                holder.commentDate.setText(commentList.comment_date);
                holder.score.setText(commentList.score);
                return convertView;
            }
        });


        sp1 = getSharedPreferences(username + "likeNum", MODE_PRIVATE);
        editor1 = sp1.edit();
        sp1.getBoolean("isLike", false);

        Record record = new Record();
        try {
            LikeNumDao likeNumDao = new LikeNumDao(getApplicationContext());
            count = likeNumDao.queryNum(bookName);
        } catch (Exception e) {
            count = 1;
        }
        record.count = count;

        final ImageView IvLike = findViewById(R.id.iv_like);

        isLike = sp1.getBoolean(bookName, false);
        Log.i("TAG_isLike", String.valueOf(isLike));
        Log.i("TAG_bookName___", bookName);

        if (!isLike) {
            IvLike.setImageResource(R.drawable.love_white);
            IvLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("TAG_bookName", bookName);
                    isLike = true;
                    IvLike.setImageResource(R.drawable.love_red);
                    editor1.putBoolean(bookName, true);
                    editor1.apply();

                    LikeNumDao likeNumDao = new LikeNumDao(getApplicationContext());
                    try {
                        likeNumDao.insert(bookName, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (likeNumDao.queryNum(bookName) == null) {
                        likeNumDao.insert(bookName, 1);
                    } else {
                        likeNumDao.updateNum(bookName);
                    }
                }
            });
        } else {
            IvLike.setImageResource(R.drawable.love_red);
        }


    }

    String book;
    String path;


    private void initWidget() {
        RlMenu = findViewById(R.id.rl_menu);
        RlMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BiQuGeListActivity.class);
                intent.putExtra("bookPath", path);
                intent.putExtra("bookName", bookName);
                startActivity(intent);
            }
        });

        btnRead = findViewById(R.id.btn_read);
        path = BiQuGeTextUtils.getTxtPath(book);


        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(v.getContext(), BiQuGeListActivity.class);
                intent.putExtra("bookPath", path);
                intent.putExtra("bookName", bookName);
                startActivity(intent);
            }
        });

        loading = findViewById(R.id.loading);
        bookLayout = findViewById(R.id.book);
        ivDetail = findViewById(R.id.iv_detail);
        name = findViewById(R.id.tv_searchName);
        author = findViewById(R.id.tv_searchAuthor);
        type = findViewById(R.id.type);
        lastUpdate = findViewById(R.id.lastUpdate);
        lastChapter = findViewById(R.id.lastChapter);
        description = findViewById(R.id.description);
        recommend = findViewById(R.id.tv_recommend);



    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        finish();
    }
}
