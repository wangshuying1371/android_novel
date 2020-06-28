package xd.wsy.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xd.wsy.R;
import xd.wsy.common.BookPageFactory;
import xd.wsy.common.MyApp;
import xd.wsy.constants.Catalogue;
import xd.wsy.dialog.MarkDialog;
import xd.wsy.helper.MarkHelper;
import xd.wsy.dao.MarkVo;
import xd.wsy.view.PageWidget;

public class ZongHengReadActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private static final String TAG = "ReadActivity";
    private static int bookBegin = 0;                         // 记录的书籍开始位置
    public static Canvas mCurPageCanvas, mNextPageCanvas;
    private Bitmap mCurPageBitmap, mNextPageBitmap;
    private static String word = "";                     // 记录当前页面的文字
    private int a = 0, b = 0;                            // 记录toolpop的位置
    private Button BtnDownload, BtnList, BtnSkip, BtnLight, BtnFont;
    private String bookPath, bookName;                             // 记录读入书的路径
    protected long count = 1;
    private ImageButton imageBtn2, imageBtn3_1, imageBtn3_2;
    private Boolean isNight;                                // 亮度模式,白天和晚上
    protected int jumpPage;                             // 记录跳转进度条
    private int light;                                        // 亮度值
    private WindowManager.LayoutParams layoutParams;
    private TextView markEdit4;
    private MarkHelper markHelper;
    private MarkDialog mDialog = null;
    private Context mContext = null;
    private PageWidget mPageWidget;
    private PopupWindow popFont, popLight, popSkip, popDownload;
    protected int PAGE = 1;
    private BookPageFactory pagefactory;
    private View toolFont, toolLight, toolSkip, toolDownload, toolList;
    public static int screenHeight;                                          // 获取屏幕高度
    public static int screenWidth;                                   //获取屏幕宽度
    private SeekBar seekBarFont, seekBarLight, seekBarSkip, seekBarDownload;
    private int text_size = 60;                               // 字体大小
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private SharedPreferences sp1;
    private SharedPreferences.Editor editor1;
    int defaultSize = 30;
    private boolean mVisible;
    private View mControlsView;
    private View mContentView;
    private final Handler mHideHandler = new Handler();
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;

    private DrawerLayout DlRead;


    // 实例化Handler
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        // 接收子线程发来的消息，同时更新UI
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    bookBegin = msg.arg1;
                    pagefactory.setM_mbBufBegin(bookBegin);
                    pagefactory.setM_mbBufEnd(bookBegin);
                    postInvalidateUI();
                    break;
                case 1:
                    pagefactory.setM_mbBufBegin(bookBegin);
                    pagefactory.setM_mbBufEnd(bookBegin);
                    postInvalidateUI();
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 读取配置文件中亮度值
     */
    private void getLight() {
        light = sp.getInt("light", 5);
        isNight = sp.getBoolean("night", false);
    }

    /**
     * 读取配置文件中字体大小
     */
    private void getSize() {
        text_size = sp.getInt("text_size", defaultSize);        ///////////////////////注意text_size = sp.getInt("size",defaultSize);
    }

    public static void getScreenSize(Context context){                    //获取屏幕尺寸，包括顶部和底部状态栏
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point outSize = new Point();
        assert windowManager != null;
        windowManager.getDefaultDisplay().getRealSize(outSize);
        screenWidth = outSize.x;
        screenHeight = outSize.y;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = getBaseContext();
        getScreenSize(this);
        mCurPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        mCurPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);
        mPageWidget = new PageWidget(this, screenWidth, screenHeight);// 页面

        setContentView(R.layout.activity_read);
        RelativeLayout relayout = findViewById(R.id.rl_read);
        relayout.addView(mPageWidget);
        bookPath = getIntent().getStringExtra("bookPath");
        bookName = getIntent().getStringExtra("bookName");
        mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);

        // 提取记录在sharedPreferences的各种状态
        sp = getSharedPreferences("read", MODE_PRIVATE);          //config指定文件名  MODE_PRIVATE被本应用读写
        sp1 = getSharedPreferences("load", MODE_PRIVATE);          //config指定文件名  MODE_PRIVATE被本应用读写
        editor = sp.edit();
        getSize();// 获取配置文件中的size大小
        getLight();// 获取配置文件中的light值
        count = sp.getLong(bookPath + "count", 1);

        layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = Math.max(light / 10.0f, 0.01f);
        getWindow().setAttributes(layoutParams);
        pagefactory = new BookPageFactory(screenWidth, screenHeight);// 书工厂    设置分辨率
        if (isNight) {
            pagefactory.setBgBitmap(BitmapFactory.decodeResource( this.getResources(), R.drawable.main_bg));    //设置背景图片
            pagefactory.setM_textColor(Color.rgb(128, 128, 128));
        } else {
            pagefactory.setBgBitmap(BitmapFactory.decodeResource( this.getResources(), R.drawable.bg));
            pagefactory.setM_textColor(Color.rgb(28, 28, 28));
        }
        bookBegin = sp.getInt(bookPath + "page", 0);

        try {
            pagefactory.openBook(bookPath, bookBegin);// 从指定位置打开书籍，默认从开始打开
            pagefactory.setM_fontSize(text_size);
            pagefactory.onDraw(mCurPageCanvas);         //将文字绘于手机屏幕
        } catch (IOException e1) {
            Log.e(TAG, "打开电子书失败", e1);
            Toast.makeText(this, "打开电子书失败", Toast.LENGTH_SHORT).show();
        }
        markHelper = new MarkHelper(this);

        mPageWidget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                boolean ret;
                if (v == mPageWidget) {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {   //停止动画。与forceFinished(boolean)相反，Scroller滚动到最终x与y位置时中止动画。
                        mPageWidget.abortAnimation();                 //计算拖拽点对应的拖拽角
                        mPageWidget.calcCornerXY(e.getX(), e.getY());     //将文字绘于当前页
                        pagefactory.onDraw(mCurPageCanvas);
                        if (mPageWidget.DragToRight()) {              // 是否从左边翻向右边
                            try {
                                pagefactory.prePage();                //显示上一页
                                bookBegin = pagefactory.getM_mbBufBegin();// 获取当前阅读位置
                                word = pagefactory.getFirstLineText();// 获取当前阅读位置的首行文字
                            } catch (IOException e1) {
                                Log.e(TAG, "onTouch->prePage error", e1);
                            }
                            if (pagefactory.isfirstPage()) {
                                Toast.makeText(mContext, "当前是第一页",Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            pagefactory.onDraw(mNextPageCanvas);
                        } else {// 右翻
                            try {
                                pagefactory.nextPage();              //显示下一页
                                bookBegin = pagefactory.getM_mbBufBegin();// 获取当前阅读位置
                                word = pagefactory.getFirstLineText();// 获取当前阅读位置的首行文字
                            } catch (IOException e1) {
                                Log.e(TAG, "onTouch->nextPage error", e1);
                            }
                            if (pagefactory.islastPage()) {
                                Toast.makeText(mContext, "已经是最后一页了", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            pagefactory.onDraw(mNextPageCanvas);
                        }
                        mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
                    }
                    editor.putInt(bookPath + "page", bookBegin).apply();
                    ret = mPageWidget.doTouchEvent(e);
                    return ret;
                }
                return false;
            }
        });

////////////////////////////////////////////////////////////////////////////////
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        initData();
        setPopupWindow();
/////////////////////////////////////////////////////////////////////////////////////////////////////
        //判断父activity是否为空，不空设置导航图标显示
        if (NavUtils.getParentActivityName(ZongHengReadActivity.this) != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            // 阅读设置按钮
            case R.id.btn_font:
                popFont.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, 0);
                seekBarFont = toolFont.findViewById(R.id.sb_font);
                text_size = sp.getInt("text_size", 25);
                seekBarFont.setProgress(text_size - 15);
                seekBarFont.setOnSeekBarChangeListener(this);
                break;

            // 亮度按钮
            case R.id.btn_light:
                popLight.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, screenWidth * 45 / 320);
                seekBarLight = toolLight.findViewById(R.id.sb_light);
                imageBtn2 = toolLight.findViewById(R.id.ib_model);
                getLight();

                seekBarLight.setProgress(light);
                if (isNight) {
                    imageBtn2.setImageResource(R.drawable.reader_switch_on);
                } else {
                    imageBtn2.setImageResource(R.drawable.reader_switch_off);
                }
                imageBtn2.setOnClickListener(this);
                seekBarLight.setOnSeekBarChangeListener(this);
                break;

            // 目录按钮
            case R.id.btn_list:
                DlRead.openDrawer(Gravity.LEFT);
//                mDlMain.setScrimColor(Color.TRANSPARENT);  //背景透明
                break;
            // 跳转按钮
            case R.id.btn_skip:
                popSkip.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, 0);
                seekBarSkip = toolSkip.findViewById(R.id.sb_skip);
                markEdit4 = toolSkip.findViewById(R.id.markEdit4);
                bookBegin = sp.getInt(bookPath + "skip", 1);
                float fPercent = (float) (bookBegin * 1.0 / pagefactory.getM_mbBufLen());
                DecimalFormat df = new DecimalFormat("#0");
                String strPercent = df.format(fPercent * 100) + "%";
                markEdit4.setText(strPercent);
                seekBarSkip.setProgress(Integer.parseInt(df.format(fPercent * 100)));
                seekBarSkip.setOnSeekBarChangeListener(this);
                break;

            //缓存按钮
            case R.id.btn_download:
                MyApp app = (MyApp) getApplication();
                List<Catalogue> list = app.getzSearchService().getDirectory();
                int size = list.size();
                int index = sp1.getInt(bookName, 10);
                popDownload.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, 0);
                seekBarDownload = toolDownload.findViewById(R.id.sb_download);
                TextView markEdit5 = toolDownload.findViewById(R.id.tv_downChapter);
                float percentLoad = (float)((index + 2) * 1.0 / size * 1.0);
                DecimalFormat df2 = new DecimalFormat("#0");
                String strPercent2 = df2.format(percentLoad * 100) + "%";
                markEdit5.setText(strPercent2);
                seekBarDownload.setProgress(Integer.parseInt(df2.format(percentLoad * 100)));
                seekBarDownload.setEnabled(false);
                break;

            // 夜间模式按钮
            case R.id.ib_model:
                if (isNight) {
                    pagefactory.setM_textColor(Color.rgb(28, 28, 28));
                    imageBtn2.setImageResource(R.drawable.reader_switch_off);
                    isNight = false;
                    pagefactory.setBgBitmap(BitmapFactory.decodeResource(
                            this.getResources(), R.drawable.bg));
                } else {
                    pagefactory.setM_textColor(Color.rgb(128, 128, 128));
                    imageBtn2.setImageResource(R.drawable.reader_switch_on);
                    isNight = true;
                    pagefactory.setBgBitmap(BitmapFactory.decodeResource(
                            this.getResources(), R.drawable.main_bg));
                }
                setLight();
                pagefactory.setM_mbBufBegin(bookBegin);
                pagefactory.setM_mbBufEnd(bookBegin);
                postInvalidateUI();
                break;

        }
    }

    public void initData(){
        MyApp app = (MyApp) getApplication();
        final List<Catalogue> infos = app.getzSearchService().getDirectory();

        BtnFont = findViewById(R.id.btn_font);
        BtnList = findViewById(R.id.btn_list);
        BtnSkip = findViewById(R.id.btn_skip);
        BtnLight = findViewById(R.id.btn_light);
        BtnDownload = findViewById(R.id.btn_download);
        DlRead = findViewById(R.id.dl_read);
        ListView mLvLeft = findViewById(R.id.lv_left);

        mLvLeft.setAdapter(new BaseAdapter() {
            final Context c = getApplicationContext();
            final class ViewHolder {
                TextView textView;
            }

            @Override
            public int getCount() {
                return infos.size();
            }

            @Override
            public Object getItem(int position) {
                return infos.get(position);
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
                    convertView = View.inflate(c, R.layout.activity_list_item, null);
                    holder.textView = convertView.findViewById(R.id.tv_list_item);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.textView.setText(infos.get(position).chapterName);
                return convertView;
            }
        });//给左边菜单写入数据
//        mLvLeft.setOnItemClickListener(this);

        BtnDownload.setOnClickListener(this);
        BtnSkip.setOnClickListener(this);
        BtnLight.setOnClickListener(this);
        BtnFont.setOnClickListener(this);
        BtnList.setOnClickListener(this);
    }

    @SuppressLint("InflateParams")
    private void setPopupWindow(){
        toolFont = getLayoutInflater().inflate(R.layout.tool_font, null);
        popFont = new PopupWindow(toolFont, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popFont.setFocusable(true);
        popFont.setOutsideTouchable(true);


        toolLight = getLayoutInflater().inflate(R.layout.tool_light, null);
        popLight = new PopupWindow(toolLight, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popLight.setFocusable(true);
        popLight.setOutsideTouchable(true);

        toolSkip = getLayoutInflater().inflate(R.layout.tool_skip, null);
        popSkip = new PopupWindow(toolSkip, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popSkip.setFocusable(true);
        popSkip.setOutsideTouchable(true);

        toolDownload = getLayoutInflater().inflate(R.layout.tool_download, null);
        popDownload = new PopupWindow(toolDownload, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popDownload.setFocusable(true);
        popDownload.setOutsideTouchable(true);

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            // 字体进度条
            case R.id.sb_font:
                text_size = seekBarFont.getProgress() + 15;
                setSize();
                pagefactory.setM_fontSize(text_size);
                pagefactory.setM_mbBufBegin(bookBegin);
                pagefactory.setM_mbBufEnd(bookBegin);
                postInvalidateUI();
                break;
            // 亮度进度条
            case R.id.sb_light:
                light = seekBarLight.getProgress();
                setLight();
                layoutParams.screenBrightness = Math.max(light / 10.0f, 0.01f);
                getWindow().setAttributes(layoutParams);
                break;
            // 跳转进度条
            case R.id.sb_skip:
                int s = seekBarSkip.getProgress();
                markEdit4.setText(s + "%");
                bookBegin = (pagefactory.getM_mbBufLen() * s) / 100;
                editor.putInt(bookPath + "skip", bookBegin).commit();
                pagefactory.setM_mbBufBegin(bookBegin);
                pagefactory.setM_mbBufEnd(bookBegin);
                try {
                    if (s == 100) {
                        pagefactory.prePage();
                        pagefactory.getM_mbBufBegin();
                        bookBegin = pagefactory.getM_mbBufEnd();
                        pagefactory.setM_mbBufBegin(bookBegin);
                        pagefactory.setM_mbBufBegin(bookBegin);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "onProgressChanged seekBar4-> IOException error", e);
                }
                postInvalidateUI();
                break;
        }
    }
    /**
     * 记录配置文件中字体大小
     */
    private void setSize() {
        try {
            text_size = seekBarFont.getProgress() + 15;
            editor.putInt("text_size", text_size);                           //////////////////////////注意  editor.putInt("size",text_size);
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, "setSize-> Exception error", e);
        }
    }
    /**
     * 记录配置文件中亮度值和横竖屏
     */
    private void setLight() {
        try {
            light = seekBarLight.getProgress();
            editor.putInt("light", light);
            if (isNight) {
                editor.putBoolean("night", true);
            } else {
                editor.putBoolean("night", false);
            }
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, "setLight-> Exception error", e);
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void toggle() {                  //切换
        if (mVisible){
            hide();
        } else {
            show();
        }
    }
    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);

    }
    private void show() {
        mContentView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        mVisible = true;
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null){
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pagefactory = null;
        mPageWidget = null;
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 刷新界面
     */
    public void postInvalidateUI() {
        mPageWidget.abortAnimation();
        pagefactory.onDraw(mCurPageCanvas);
        try {
            pagefactory.currentPage();
            bookBegin = pagefactory.getM_mbBufBegin();// 获取当前阅读位置
            word = pagefactory.getFirstLineText();// 获取当前阅读位置的首行文字
        } catch (IOException e1) {
            Log.e(TAG, "postInvalidateUI->IOException error", e1);
        }
        pagefactory.onDraw(mNextPageCanvas);
        mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
        mPageWidget.postInvalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

/////////书签/////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setIconEnable(menu, true);
        new MenuInflater(this).inflate(R.menu.activity_read_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_mark:
                SQLiteDatabase db = markHelper.getWritableDatabase();
                try {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm ss");
                    String time = sf.format(new Date());
                    db.execSQL(
                            "insert into markHelper (path, mark, word, time) values (?,?,?,?)",
                            new String[] { bookPath, bookBegin + "", word, time });
                    db.close();
                    Toast.makeText(ZongHengReadActivity.this, "书签添加成功", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    Toast.makeText(ZongHengReadActivity.this, "该书签已存在", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ZongHengReadActivity.this, "添加书签失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_my_mark:
                SQLiteDatabase dbSelect = markHelper.getReadableDatabase();
                String[] col = { "mark", "word", "time" };
                Cursor cur = dbSelect.query("markHelper", col, "path = '" + bookPath + "'", null, null, null, null);
                int num = cur.getCount();
                if (num == 0) {
                    Toast.makeText(ZongHengReadActivity.this, "您还没有书签", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<MarkVo> markList = new ArrayList<MarkVo>();
                    while (cur.moveToNext()) {
                        String s1 = cur.getString(cur.getColumnIndex("word"));
                        String s2 = cur.getString(cur.getColumnIndex("time"));
                        int b1 = cur.getInt(cur.getColumnIndex("mark"));
                        int p = 0;
                        int count = 10;
                        MarkVo mv = new MarkVo(s1, p, count, b1, s2, bookPath);
                        markList.add(mv);
                    }
                    mDialog = new MarkDialog(this, markList, mHandler, R.style.FullHeightDialog);
                    mDialog.setCancelable(false);
                    mDialog.setTitle("我的书签");
                    mDialog.setCanceledOnTouchOutside(true);
                    mDialog.show();
                }
                dbSelect.close();
                cur.close();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

///////////////////////menu菜单的图标///////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setIconEnable(Menu menu,boolean enable){
        try {
            Class<?> clazz = Class.forName("androidx.appcompat.view.menu.MenuBuilder");
            Method method = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            method.setAccessible(true);
            method.invoke(menu, enable);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
