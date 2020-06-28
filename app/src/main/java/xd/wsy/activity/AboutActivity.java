package xd.wsy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.os.Bundle;

import xd.wsy.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);



        //判断父activity是否为空，不空设置导航图标显示
       /* if (NavUtils.getParentActivityName(AboutActivity.this) != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/
    }
}
