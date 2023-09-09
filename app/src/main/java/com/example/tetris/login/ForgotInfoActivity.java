package com.example.tetris.login;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.room.Room;

import com.example.tetris.R;
import com.example.tetris.dao.UserDao;
import com.example.tetris.db.DataBase;
import com.example.tetris.entity.User;

public class ForgotInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView text_title;
    private EditText edit_account, edit_password, edit_password_again;
    private Button btn_revise, btn_cancel;
    //    private DBHelper dbHelper;
    private DataBase db;
    private UserDao userDao;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_info);
        init();
    }

    private void init() {
        flag = false;
        text_title = (TextView) findViewById(R.id.text_title);
//        Typeface typeface = ResourcesCompat.getFont(this, R.font.zhao);
//        text_title.setTypeface(typeface);

        //获取数据库对象,且对比Data.db中的username是否存在，存在则进行修改密码操作
        //连接数据库
        db = Room.databaseBuilder(getApplicationContext(), DataBase.class, "mydb")
                .allowMainThreadQueries()
                .build();
        userDao = db.userDao();

        edit_account = (EditText) findViewById(R.id.edit_account);
        edit_account.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_account.clearFocus();
                }
                return false;
            }
        });

        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String s = v.getText().toString();
                    //设置密码长度有问题，判断editText的输入长度需要重新理解
                    System.out.println(" v: ****** v :" + s.length());
                    if (s.length() >= 6) {
                        System.out.println(" ****** s :" + s.length());
                        edit_password.clearFocus();
                        InputMethodManager imm =
                                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edit_password.getWindowToken(), 0);
                    } else {
                        Toast.makeText(ForgotInfoActivity.this, "密码设置最少为6位！", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        edit_password_again = (EditText) findViewById(R.id.edit_password_again);
        edit_password_again.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_password_again.clearFocus();
                    InputMethodManager im =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(edit_password_again.getWindowToken(), 0);
                }
                return false;
            }
        });

        //点击修改按钮，进行修改密码操作，并返回主界面，并将修改后的密码保存到数据库中
        btn_revise = (Button) findViewById(R.id.btn_revise);
        btn_revise.setOnClickListener(this);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_revise:
                if (checkUser(edit_account.getText().toString())) {
                    if (edit_password_again.getText().toString().length() < 6) {
                        Toast.makeText(ForgotInfoActivity.this, "密码设置最少为6位！", Toast.LENGTH_SHORT).show();
                    } else {
                        revisePassword(edit_account.getText().toString(), edit_password_again.getText().toString());
                        Toast.makeText(ForgotInfoActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
            case R.id.btn_cancel:

                finish();
                break;
        }
    }

    /**
     * 更新密码
     *
     * @param value    用户名
     * @param password 密码
     */
    private void revisePassword(String value, String password) {
        User user = userDao.findUserName(value);
        user.password = password;
        userDao.update(user);
    }

    /**
     * 检验用户名是否已经注册且原密码是否正确
     */
    private boolean checkUser(String value) {
        User user = userDao.findUserName(value);

        if (user== null){
            Toast.makeText(ForgotInfoActivity.this, "用户名不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
        else  if (!user.password.equals(edit_password.getText().toString())) {
            Toast.makeText(ForgotInfoActivity.this, "原密码错误！", Toast.LENGTH_SHORT).show();
            return  false;
        }
        else return true;
    }
}