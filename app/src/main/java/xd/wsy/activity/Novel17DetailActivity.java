package xd.wsy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import java.util.Date;
import java.util.List;

import xd.wsy.common.MyApp;
import xd.wsy.R;
import xd.wsy.constants.CommentList;
import xd.wsy.constants.Novel17BookDetail;
import xd.wsy.constants.Record;
import xd.wsy.dao.CommentDao;
import xd.wsy.dao.LikeNumDao;
import xd.wsy.dao.UserDao;
import xd.wsy.service.Novel17SearchService;
import xd.wsy.utils.Novel17TextUtils;

public class Novel17DetailActivity extends AppCompatActivity {

    Button BtnRead;
    String bookName, bookPath, bookUrl;
    TextView name, type, lastUpdate, lastChapter, description;
    RelativeLayout RlBook;
    ImageView IvBookPicture;
    LinearLayout loading;
    RelativeLayout RlMenu;

    private SharedPreferences sp;
    private SharedPreferences sp1;
    private SharedPreferences.Editor editor1;
    int count;
    Boolean isLike;

    @SuppressLint({"HandlerLeak", "LongLogTag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel17_detail);
        initData();

        String bookUrl_detail = getIntent().getStringExtra("bookUrl");
        MyApp app = (MyApp) getApplication();
        final Novel17SearchService searchService = app.getnSearchService();
        if (!bookUrl_detail.equals("")) {
            loading.setVisibility(View.VISIBLE);
            Novel17BookDetail bookDetail = searchService.getCache(bookUrl_detail);
            if (bookDetail != null) {
                loading.setVisibility(View.GONE);
                showBookDetail(bookDetail, searchService);
            } else {
                searchService.get(bookUrl_detail, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        loading.setVisibility(View.GONE);
                        switch (msg.what) {
                            case 1:
                                Novel17BookDetail bookDetail1 = (Novel17BookDetail) msg.obj;
                                if (bookDetail1 != null)
                                    showBookDetail(bookDetail1, searchService);
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
    private void showBookDetail(final Novel17BookDetail bookDetail, final Novel17SearchService searchService) {
        RlBook.setVisibility(View.VISIBLE);
        final Handler h = new Handler() {
            @SuppressLint("LongLogTag")
            public void handleMessage(Message msg) {
                Bitmap bm = (Bitmap) msg.obj;
                if (bm != null)
                    IvBookPicture.setImageBitmap(bm);
            }
        };
        new Thread(new Runnable() {

            @SuppressLint("LongLogTag")
            @Override
            public void run() {
                byte[] bytes = searchService.getBytes(bookDetail.pictureSrc);
                if (bytes != null) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    h.obtainMessage(1, bm).sendToTarget();
                }
            }
        }).start();

        bookName = bookDetail.name;
        name.setText(bookDetail.name);
        type.setText(bookDetail.type);
        lastChapter.setText(bookDetail.lastChapter);
        lastUpdate.setText(bookDetail.lastUpdateTime);
        description.setText("        " + bookDetail.description);



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
                final WindowManager.LayoutParams lp = Novel17DetailActivity.this.getWindow().getAttributes();
                lp.alpha = 0.3f;//代表透明程度，范围为0 - 1.0f
                Novel17DetailActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                Novel17DetailActivity.this.getWindow().setAttributes(lp);
            }
        });
        /**
         * 退出popupWindow时取消暗背景
         */
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                final WindowManager.LayoutParams lp = Novel17DetailActivity.this.getWindow().getAttributes();
                lp.alpha = 1.0f;
                Novel17DetailActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                Novel17DetailActivity.this.getWindow().setAttributes(lp);
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


    @SuppressLint("LongLogTag")
    private void initData() {
        BtnRead = findViewById(R.id.btn_read);
        RlBook = findViewById(R.id.rl_book);
        IvBookPicture = findViewById(R.id.iv_bookPicture);
        name = findViewById(R.id.tv_name);
        type = findViewById(R.id.tv_type);
        lastUpdate = findViewById(R.id.tv_lastUpdate);
        lastChapter = findViewById(R.id.tv_lastChapter);
        description = findViewById(R.id.tv_description);
        loading = findViewById(R.id.loading);
        RlMenu = findViewById(R.id.rl_menu);

        bookPath = Novel17TextUtils.getTxtPath(bookName);

        RlMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Novel17ListActivity.class);
                intent.putExtra("bookPath", bookPath);
                intent.putExtra("bookName", bookName);
                startActivity(intent);
            }
        });

        BtnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(v.getContext(), Novel17ListActivity.class);
                intent.putExtra("bookPath", bookPath);
                intent.putExtra("bookName", bookName);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
