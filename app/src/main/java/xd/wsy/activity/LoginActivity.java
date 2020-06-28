package xd.wsy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import xd.wsy.R;
import xd.wsy.dao.UserDao;

public class LoginActivity extends AppCompatActivity {

    EditText EtPhone, EtPassword;
    Button BtnLogin, BtnRegister;
    UserDao userDao;
    String phone, password;
    SharedPreferences sp;
    private SharedPreferences.Editor editor;
    CheckBox CbRememberPwd, CbAutoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EtPhone = findViewById(R.id.et_loginPhone);
        EtPassword = findViewById(R.id.et_loginPassword);
        BtnLogin = findViewById(R.id.btn_login);
        BtnRegister = findViewById(R.id.btn_register);
        CbRememberPwd = findViewById(R.id.cb_rememberPwd);
        CbAutoLogin = findViewById(R.id.cb_autoLogin);

        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        if (sp.getBoolean("ISCHECK", false)){
            //设置默认是记录密码状态
            CbRememberPwd.setChecked(true);
            EtPhone.setText(sp.getString("phone", ""));
            EtPassword.setText(sp.getString("password", ""));

            //判断自动登录多选框状态
            if (sp.getBoolean("AUTO_ISCHECK", false)){
                //设置默认是自动登录状态
                CbAutoLogin.setChecked(true);

                //跳转界面
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = EtPhone.getText().toString();
                password = EtPassword.getText().toString();
                userDao = new UserDao(getApplicationContext());
                if (userDao.query(phone, password)) {
                    if (CbRememberPwd.isChecked()) {
                        editor = sp.edit();
                        editor.putString("phone", phone);
                        editor.putString("password", password);
                        editor.apply();
                    }
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "还没有这个账号", Toast.LENGTH_SHORT).show();
                }
            }
        });
        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //监听记住密码多选框按钮事件
        CbRememberPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (CbRememberPwd.isChecked()) {
                    System.out.println("记住密码已选中");
                    sp.edit().putBoolean("ISCHECK", true).apply();
                }else {
                    System.out.println("记住密码没有选中");
                    sp.edit().putBoolean("ISCHECK", false).apply();
                }
            }
        });

        //监听自动登录多选框事件
        CbAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (CbAutoLogin.isChecked()) {
                    System.out.println("自动登录已选中");
                    sp.edit().putBoolean("AUTO_ISCHECK", true).apply();

                } else {
                    System.out.println("自动登录没有选中");
                    sp.edit().putBoolean("AUTO_ISCHECK", false).apply();
                }
            }
        });
    }

}
