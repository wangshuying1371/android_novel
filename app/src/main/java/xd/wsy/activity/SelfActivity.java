package xd.wsy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import xd.wsy.R;
import xd.wsy.dao.UserDao;

public class SelfActivity extends AppCompatActivity {

    ImageView IvPicture;
    SharedPreferences sp;
    private SharedPreferences.Editor editor;
    String name = "xiaohong";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self);

        IvPicture = findViewById(R.id.iv_picture);
        IvPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelfActivity.this, SelfDataActivity.class);
                startActivity(intent);
            }
        });

        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        final String phone = sp.getString("phone", String.valueOf(0));
        TextView TvName = findViewById(R.id.tv_name);

        final UserDao userDao = new UserDao(getApplicationContext());
        try {
            name = userDao.queryName(phone);
            TvName.setText(name);
        }catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG_userDao", "userDao出错");
        }

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(sp.getString("picturePath", "sdcard/Download/timg2"));
//            Bitmap bitmap1 = ClipSquareBitmap(bitmap, 200, bitmap.getWidth());
            Bitmap bitmap1 = ClipSquareBitmap(bitmap, 200, 200);
            IvPicture.setImageBitmap(bitmap1);
        }catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG_picture", "picture出错");
        }

        TextView TvOutLogin = findViewById(R.id.tv_outLogin);
        TvOutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog mBottomDialog = new Dialog(SelfActivity.this, R.style.bottom_dialog);
                LinearLayout root = (LinearLayout) LayoutInflater.from(SelfActivity.this).inflate(R.layout.dialog_outlogin, null);
                root.findViewById(R.id.btn_confirmOut).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor = sp.edit();
                        editor.putBoolean("ISCHECK", false);
                        editor.putBoolean("AUTO_ISCHECK", false);
                        editor.apply();

                        finish();
                        Intent intent = new Intent(SelfActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
                root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomDialog.cancel();
                    }
                });
                mBottomDialog.setContentView(root);
                Window dialogWindow = mBottomDialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM); //设置显示在底部
                dialogWindow.setWindowAnimations(R.style.dialog_style);  //显示划出效果
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.x = 0;
                lp.y = -20;
                lp.width = getResources().getDisplayMetrics().widthPixels; // 宽度
                root.measure(0, 0);
                lp.height = root.getMeasuredHeight();
                lp.alpha = 9f;
                dialogWindow.setAttributes(lp);
                mBottomDialog.setCanceledOnTouchOutside(true);
                mBottomDialog.show();
            }
        });

        TextView TvLogout = findViewById(R.id.tv_logout);
        TvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog mBottomDialog = new Dialog(SelfActivity.this, R.style.bottom_dialog);
                LinearLayout root = (LinearLayout) LayoutInflater.from(SelfActivity.this).inflate(R.layout.dialog_logout, null);
                root.findViewById(R.id.btn_confirmLogout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userDao.delete(phone);
                        editor = sp.edit();
                        editor.putBoolean("ISCHECK", false);
                        editor.putBoolean("AUTO_ISCHECK", false);
                        editor.apply();

                        finish();
                        Intent intent = new Intent(SelfActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
                root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomDialog.cancel();
                    }
                });
                mBottomDialog.setContentView(root);
                Window dialogWindow = mBottomDialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM); //设置显示在底部
                dialogWindow.setWindowAnimations(R.style.dialog_style);  //显示划出效果
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.x = 0;
                lp.y = -20;
                lp.width = getResources().getDisplayMetrics().widthPixels; // 宽度
                root.measure(0, 0);
                lp.height = root.getMeasuredHeight();
                lp.alpha = 9f;
                dialogWindow.setAttributes(lp);
                mBottomDialog.setCanceledOnTouchOutside(true);
                mBottomDialog.show();
            }
        });
    }

    public static Bitmap ClipSquareBitmap(Bitmap bmp, int width, int radius) {
        if (bmp == null || width <= 0)
            return null;
        //如果图片比较小就没必要进行缩放了

        /**
         * 把图片进行缩放，以宽高最小的一边为准，缩放图片比例
         * */
        if (bmp.getWidth() > width && bmp.getHeight() > width) {
            if (bmp.getWidth() > bmp.getHeight()) {
                bmp = Bitmap.createScaledBitmap(bmp, (int) (((float) width) * bmp.getWidth() / bmp.getHeight()), width, false);
            } else {
                bmp = Bitmap.createScaledBitmap(bmp, width, (int) (((float) width) * bmp.getHeight() / bmp.getWidth()), false);
            }

        } else {
            width = bmp.getWidth() > bmp.getHeight() ? bmp.getHeight() : bmp.getWidth();
            Log.d("zeyu","宽" + width + ",w" + bmp.getWidth() + ",h" + bmp.getHeight());
            if (radius > width) {
                radius = width;
            }
        }
        Bitmap output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        //设置画笔全透明
        canvas.drawARGB(0, 0, 0, 0);
        Paint paints = new Paint();
        paints.setColor(Color.WHITE);
        paints.setAntiAlias(true);//去锯齿
        paints.setFilterBitmap(true);
        //防抖动
        paints.setDither(true);

        //把图片圆形绘制矩形
        if (radius <= 0)
            canvas.drawRect(new Rect(0, 0, width, width), paints);
        else //绘制圆角
            canvas.drawRoundRect(new RectF(0, 0, width, width), radius, radius, paints);
        // 取两层绘制交集。显示前景色。
        paints.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect rect = new Rect();
        if (bmp.getWidth() >= bmp.getHeight()) {
            rect.set((bmp.getWidth() - width) / 2, 0, (bmp.getWidth() + width) / 2, width);
        } else {
            rect.set(0, (bmp.getHeight() - width) / 2, width, (bmp.getHeight() + width) / 2);
        }
        Rect rect2 = new Rect(0, 0, width, width);
        //第一个rect 针对bmp的绘制区域，rect2表示绘制到上面位置
        canvas.drawBitmap(bmp, rect, rect2, paints);
        bmp.recycle();
        return output;
    }

    /*public void onClick_MyDownLoad(View view) {
        Intent intent = new Intent(SelfActivity.this, MyLoadActivity.class);
        startActivity(intent);
    }*/

    public void onClick_MyUpdate(View view) {
        Toast.makeText(this, "正在检查更新...", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "当前是最新版本", Toast.LENGTH_SHORT).show();
    }

    public void onClick_MyAbout(View view) {
        Intent intent = new Intent(SelfActivity.this, AboutActivity.class);
        startActivity(intent);
    }

}
