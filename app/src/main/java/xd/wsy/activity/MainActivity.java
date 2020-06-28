package xd.wsy.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import xd.wsy.R;

public class MainActivity extends AppCompatActivity {

    LocalActivityManager manager;
    private RadioGroup radioGroup;
    private ViewPager mPager;
    // Tab页面列表
    private List<View> listViews;

    public static final class MyPagerAdapter extends PagerAdapter {

        private List<View> lists;

        public MyPagerAdapter(List<View> mListViews) {
            this.lists = mListViews;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(lists.get(position));
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(lists.get(position), 0);
            return lists.get(position);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == (object);
        }

        @Override
        public void restoreState(@Nullable Parcelable state, @Nullable ClassLoader loader) {
            super.restoreState(state, loader);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        // 禁止方法在Activity 的onCreate方法中使用：
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);  //隐藏软键盘
        manager = new LocalActivityManager(this, false);
        manager.dispatchCreate(savedInstanceState);
        initWidget();

    }

//    private static final String index[] = { "1", "2", "3" ,"4"};
    private static final String index[] = { "1", "2", "3" };

    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }
    private void initWidget() {
        mPager = findViewById(R.id.viewpager_main);
        listViews = new ArrayList<>();
        MainActivity.MyPagerAdapter mpAdapter = new MainActivity.MyPagerAdapter(listViews);

        Intent appIntent = new Intent(this, LocalActivity.class);
        listViews.add(getView(index[0], appIntent));
        Intent connectIntent = new Intent(this, HomePageActivity.class);
        listViews.add(getView(index[1], connectIntent));
        /*Intent sceneIntent = new Intent(this, UpLoadActivity.class);
        listViews.add(getView(index[2], sceneIntent));*/
        Intent wodeIntent = new Intent(this, SelfActivity.class);
        listViews.add(getView(index[2],wodeIntent));

        mPager.setAdapter(mpAdapter);
        mPager.setCurrentItem(1);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());

        radioGroup = findViewById(R.id.view_main_radio);
        radioGroup.check(R.id.view_radio_connect);
        radioGroup.setOnCheckedChangeListener(new MainActivity.MyRadioCheckListener(mPager));
    }

    public static final class MyRadioCheckListener implements RadioGroup.OnCheckedChangeListener {

        ViewPager pager;
        public MyRadioCheckListener(ViewPager pager) {
            this.pager = pager;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.view_radio_app:
                    pager.setCurrentItem(0);
                    break;
                case R.id.view_radio_connect:
                    pager.setCurrentItem(1);
                    break;
                /*case R.id.view_radio_scene:
                    pager.setCurrentItem(2);
                    break;*/
                case R.id.view_radio_self:
                    pager.setCurrentItem(3);
                    break;
                default:
                    break;
            }
        }
    }

    //滑动切换图标
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {        }

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    radioGroup.check(R.id.view_radio_app);
                    break;
                case 1:
                    radioGroup.check(R.id.view_radio_connect);
                    break;
                /*case 2:
                    radioGroup.check(R.id.view_radio_scene);
                    break;*/
                case 3:
                    radioGroup.check(R.id.view_radio_self);
                    break;
            }
        }
    }
}
