package xd.wsy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import xd.wsy.R;

public class HomePageActivity extends AppCompatActivity {

    List<Intent> intentList;
    LocalActivityManager activityManager;

    String[] tabIds={"home","task","record","profile","more"};

    private RadioGroup radioGroup;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //activity页
        activityManager=new LocalActivityManager(this,true);
        activityManager.dispatchCreate(savedInstanceState);

        intentList= new LinkedList<>();
        Intent firstIntent=new Intent(this, Novel17Activity.class);
        intentList.add(firstIntent);

        Intent secondIntent=new Intent(this, BiQuGeActivity.class);
        intentList.add(secondIntent);

        Intent thirdIntent=new Intent(this, ZongHengActivity.class);
        intentList.add(thirdIntent);

        //设置radioGroup
        radioGroup= findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_novel17:
                        viewpager.setCurrentItem(0);
                        break;
                    case R.id.radio_biQuGe:
                        viewpager.setCurrentItem(1);
                        break;
                    case R.id.radio_tianShu:
                        viewpager.setCurrentItem(2);
                        break;

                }
            }

        });

        //设置viewpager
        viewpager= findViewById(R.id.viewpager);
        viewpager.setAdapter(new MyPageAdapter());

        /*viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrollStateChanged(int position) {
                // TODO 自动生成的方法存根

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO 自动生成的方法存根

            }

            @Override
            public void onPageSelected(int position) {
                // TODO 自动生成的方法存根
                switch(position){
                    case 0:
                        radioGroup.check(R.id.radio_novel17);
                        break;
                    case 1:
                        radioGroup.check(R.id.radio_biQuGe);
                        break;
                    case 2:
                        radioGroup.check(R.id.radio_tianShu);
                        break;

                }
            }

        });*/

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        radioGroup.check(R.id.radio_novel17);
                        break;
                    case 1:
                        radioGroup.check(R.id.radio_biQuGe);
                        break;
                    case 2:
                        radioGroup.check(R.id.radio_tianShu);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /*protected void onResume(){
        super.onResume();
        activityManager.dispatchResume();
    }

    protected void onPause(){
        super.onPause();
        activityManager.dispatchPause(isFinishing());
    }

    protected void onStop(){
        super.onStop();
        activityManager.dispatchStop();
    }

    protected void onDestory(){
        super.onDestroy();
        activityManager.dispatchDestroy(isFinishing());
    }*/

    private class MyPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return intentList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        HashMap<String, View> idViewMap;
        public MyPageAdapter() {
            idViewMap= new HashMap<>();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ViewPager viewPager = (ViewPager) container;
            View tabView = idViewMap.get(tabIds[position]);
            viewPager.removeView(tabView);
//            activityManager.destroyActivity(tabIds[position], true);
            idViewMap.remove(tabIds[position]);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ViewPager viewPager = (ViewPager) container;
            View tabView = idViewMap.get(tabIds[position]);
            if (tabView == null) {
                tabView = activityManager.startActivity(tabIds[position], intentList.get(position)).getDecorView();
                idViewMap.put(tabIds[position], tabView);
            } else {
                ViewGroup tabViewParent = (ViewGroup) tabView.getParent();
                if (tabViewParent != null) {
                    tabViewParent.removeAllViewsInLayout();
                }
            }
            viewPager.addView(tabView);
            return tabView;
        }
    }
    /*private class MyPageAdapter extends PagerAdapter {
        //临时保存view
        HashMap<String, View> idViewMap;
        public MyPageAdapter(){            idViewMap= new HashMap<>();        }
        public int getItemPosition(Object object) {            return POSITION_NONE;        }
        @Override
        public void destroyItem(View view, int position, Object arg2) {
            ViewPager viewPager = ((ViewPager) view);
            View tabView=idViewMap.get(tabIds[position]);
            viewPager.removeView(tabView);//移除viewPager中的view
            activityManager.destroyActivity(tabIds[position], true);//销毁activity
            idViewMap.remove(tabIds[position]);//移除idViewMap中的引用
        }
        @Override
        public void finishUpdate(View arg0) {        }
        @Override
        public int getCount() {            return intentList.size();        }
        @Override
        public Object instantiateItem(View view, int position) {
            ViewPager viewPager = ((ViewPager) view);
            View tabView=idViewMap.get(tabIds[position]);//先向idViewMap获取，看有没有
            if(tabView==null){//没有就新创建一个
                tabView=activityManager.startActivity(tabIds[position], intentList.get(position)).getDecorView();
                idViewMap.put(tabIds[position], tabView);
            }else{//检查是否已加入到某个parent view中
                ViewGroup tabViewParent = (ViewGroup) tabView.getParent();
                if (tabViewParent != null) {
                    tabViewParent.removeAllViewsInLayout();
                }
            }
            viewPager.addView(tabView);
            return tabView;
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {            return arg0 == arg1;        }
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {        }
        @Override
        public Parcelable saveState() {            return null;        }
        @Override
        public void startUpdate(View arg0) {        }
    }*/
}
