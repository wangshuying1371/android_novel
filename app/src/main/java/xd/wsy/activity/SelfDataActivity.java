package xd.wsy.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import xd.wsy.R;
import xd.wsy.dao.UserDao;

import static xd.wsy.activity.SelfActivity.ClipSquareBitmap;

public class SelfDataActivity extends AppCompatActivity {

    ImageView IvSelfPicture;
    RelativeLayout RlPassword, RlPhone, RlEmail;
    SharedPreferences sp;
    private SharedPreferences.Editor editor;
    TextView TvEmail, TvPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_data);

        IvSelfPicture = findViewById(R.id.iv_selfPicture);
        RlPassword = findViewById(R.id.rl_selfPassword);
        TvEmail = findViewById(R.id.tv_selfEmail);
        TvPhone = findViewById(R.id.tv_selfPhone);


        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String phone = sp.getString("phone", String.valueOf(0));
        Log.i("TAG_phone00", phone);


        Bitmap bitmap = BitmapFactory.decodeFile(sp.getString("picturePath", "sdcard/Download/timg2"));
//        Bitmap bitmap1 = ClipSquareBitmap(bitmap, 200, bitmap.getWidth());
        Bitmap bitmap1 = ClipSquareBitmap(bitmap, 200, 200);
        IvSelfPicture.setImageBitmap(bitmap1);
        IvSelfPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelfDataActivity.this, FolderActivity.class);
                startActivityForResult(intent, 0);
            }
        });


        final UserDao userDao = new UserDao(getApplicationContext());
        String email = userDao.queryEmail(phone);
        TvEmail.setText(email);
        TvPhone.setText(phone);

        final View view = getLayoutInflater().inflate(R.layout.pop_password, null);
        final PopupWindow popupWindow = new PopupWindow(view, 1000, 600);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        RlPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAtLocation(RlPassword, Gravity.CENTER, 0, 200);
                /**
                 * 点击popupWindow让背景变暗
                 */
                final WindowManager.LayoutParams lp = SelfDataActivity.this.getWindow().getAttributes();
                lp.alpha = 0.3f;//代表透明程度，范围为0 - 1.0f
                SelfDataActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                SelfDataActivity.this.getWindow().setAttributes(lp);
            }
        });



        /**
         * 退出popupWindow时取消暗背景
         */
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                final WindowManager.LayoutParams lp = SelfDataActivity.this.getWindow().getAttributes();
                lp.alpha = 1.0f;
                SelfDataActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                SelfDataActivity.this.getWindow().setAttributes(lp);
            }
        });

        final EditText EtOldPassword = view.findViewById(R.id.et_oldPassword);
        final EditText EtNewPassword = view.findViewById(R.id.et_newPassword);
        Button BtnConfirmUpdate = view.findViewById(R.id.btn_confirmUpdate);


        BtnConfirmUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldPassword = EtOldPassword.getText().toString();
                final String newPassword = EtNewPassword.getText().toString();
                Log.i("TAG_oldPassword", oldPassword);
                Log.i("TAG_newPassword", newPassword);

                userDao.updatePassword(oldPassword, newPassword);
                Toast.makeText(SelfDataActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 2) {
            String PicturePath = data.getStringExtra("picturePath");
            editor = sp.edit();
            editor.putString("picturePath", PicturePath);
            editor.apply();

            IvSelfPicture.setImageBitmap(BitmapFactory.decodeFile(sp.getString("picturePath", "sdcard/Download/timg1")));

        }
    }
}
