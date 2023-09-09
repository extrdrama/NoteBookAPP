package com.example.tetris.login;

import android.content.Intent;
import android.graphics.Typeface;
import android.text.InputFilter;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView text_title;
    private EditText edit_register, edit_setpassword, edit_resetpassword;
    private Button btn_yes, btn_cancel;
    //    private DBHelper dbHelper;
    private DataBase db;
    private UserDao userDao;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        flag = false;
        text_title = (TextView) findViewById(R.id.text_title);
        //使用字体
//        Typeface typeface = ResourcesCompat.getFont(this, R.font.yan);
//        text_title.setTypeface(typeface);

        edit_register = findViewById(R.id.edit_register);
        edit_register.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) &&
                            !Character.toString(source.charAt(i)).equals("_")) {
                        Toast.makeText(RegisterActivity.this, "只能使用'_'、字母、数字、汉字注册！", Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        }
        });
        edit_register.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String temp = edit_register.getText().toString();
                    if (temp.length() < 6) {
                        Toast.makeText(RegisterActivity.this, "用户名不能小于6位", Toast.LENGTH_SHORT).show();
                    } else {
                        edit_register.clearFocus();
                        InputMethodManager imm =
                                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edit_register.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
        edit_register.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                } else {
                    String temp = edit_register.getText().toString();
                    if (temp.length() < 6) {
                        Toast.makeText(RegisterActivity.this, "用户名不能小于6位", Toast.LENGTH_SHORT).show();
                    } else flag = true;
                }
            }
        });

        edit_setpassword = findViewById(R.id.edit_setpassword);
        edit_setpassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String s = edit_setpassword.getText().toString();
                    System.out.println(" v: ****** v :" + s.length());
                    if (s.length() >= 6) {
                        System.out.println(" ****** s :" + s.length());
                        edit_setpassword.clearFocus();
                        InputMethodManager imm =
                                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edit_setpassword.getWindowToken(), 0);
                    } else {
                        Toast.makeText(RegisterActivity.this, "密码设置最少为6位！", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        edit_setpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                } else {
                    String temp = edit_setpassword.getText().toString();
                    if (temp.length() < 6) {
                        Toast.makeText(RegisterActivity.this, "密码设置最少为6位", Toast.LENGTH_SHORT).show();
                    } else flag = true;
                }
            }
        });

        edit_resetpassword = (EditText) findViewById(R.id.edit_resetpassword);
        edit_resetpassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_resetpassword.clearFocus();
                    InputMethodManager im =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(edit_resetpassword.getWindowToken(), 0);
                }
                return false;
            }
        });

        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(this);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);

        //连接数据库
        db = Room.databaseBuilder(getApplicationContext(), DataBase.class, "mydb")
//                                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        userDao = db.userDao();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                if (CheckIsDataAlreadyInDBorNot(edit_register.getText().toString())) {
                    Toast.makeText(this, "该用户名已被注册，注册失败", Toast.LENGTH_SHORT).show();
                } else {
                    if (!flag) {
                        Toast.makeText(this, "用户名或密码不能小于6位！", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_setpassword.getText().toString().trim().
                                equals(edit_resetpassword.getText().toString())) {
                            registerUserInfo(edit_register.getText().toString(),
                                    edit_setpassword.getText().toString());
                            Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();

                            finish();
                        } else {
                            Toast.makeText(this, "两次输入密码不同，请重新输入！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 注册用户
     */
    private void registerUserInfo(String username, String userpassword) {
        User user = new User();
        user.username = username;
        user.password = userpassword;
        userDao.insert(user);
    }

    /**
     * 检验用户名是否已经注册
     */
    public boolean CheckIsDataAlreadyInDBorNot(String value) {
        User user = userDao.findUserName(value);
        if (user != null) return true;
        return false;
    }

}