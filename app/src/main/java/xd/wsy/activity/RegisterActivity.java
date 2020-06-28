package xd.wsy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import xd.wsy.R;
import xd.wsy.dao.UserDao;

public class RegisterActivity extends AppCompatActivity {

    UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button BtnRegister = findViewById(R.id.tv_register);
        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        EditText EtName = findViewById(R.id.et_registerName);
        EditText EtPhone = findViewById(R.id.et_registerPhone);
        EditText EtEmail = findViewById(R.id.et_registerEmail);
        EditText EtPassword = findViewById(R.id.et_registerPassword);
        EditText EtPasswordConfirm = findViewById(R.id.et_registerConfirm);

        String username = EtName.getText().toString();
        String phone = EtPhone.getText().toString();
        String email = EtEmail.getText().toString();
        String password = EtPassword.getText().toString();
        String passwordConfirm = EtPasswordConfirm.getText().toString();



        if (username.length() < 1){
            Toast.makeText(this,"用户名不能为空.",Toast.LENGTH_LONG).show();
            return;
        } else if (phone.length() < 1){
            Toast.makeText(this,"手机号不能为空.",Toast.LENGTH_LONG).show();
            return;
        } else if (phone.length() < 11){
            Toast.makeText(this,"手机号格式不正确.",Toast.LENGTH_LONG).show();
            return;
        } else if (email.length() < 1){
            Toast.makeText(this,"邮箱不能为空.",Toast.LENGTH_LONG).show();
            return;
        } else if (!email.contains("@") && !email.contains(".com")) {
            Toast.makeText(this,"邮箱格式不对.",Toast.LENGTH_LONG).show();
            return;
        } else if (password.length() < 1){
            Toast.makeText(this,"密码不能为空.",Toast.LENGTH_LONG).show();
            return;
        } else if (!password.equals(passwordConfirm)){
            Toast.makeText(this,"两次密码不相同.", Toast.LENGTH_LONG).show();
            return;
        }


        userDao = new UserDao(getApplicationContext());
        userDao.insert(username,phone, email, password, passwordConfirm);
        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);

    }
}
