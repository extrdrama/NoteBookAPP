package com.example.tetris.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tetris.R;
import com.example.tetris.dao.UserDao;
import com.example.tetris.db.DataBase;
import com.example.tetris.entity.User;
import com.example.tetris.login.ForgotInfoActivity;
import com.example.tetris.login.LoginActivity;
import com.example.tetris.others.AboutUsActivity;
import com.example.tetris.others.VersionActivity;

public class PersonActivity extends AppCompatActivity {
    private ImageView myPhotoImageView;
    private TextView accountTextView;
    private TextView settingAccountTextView;
    private TextView myListTextView;
    private TextView modifyPasswordTextView;
    private TextView noneTextView;
    private Button exitButton;
    private Button deleteButton;
    private DataBase db;
    private   String receivedUsername;
    private UserDao userDao;
    private int clickCount = 0;
    private long startTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        init();
    }
    private void init(){
        myPhotoImageView = findViewById(R.id.myPhoto);
        accountTextView = findViewById(R.id.account);
        settingAccountTextView = findViewById(R.id.setting_tv_account);
        myListTextView = findViewById(R.id.mylist);
        modifyPasswordTextView = findViewById(R.id.changepwd);
        noneTextView = findViewById(R.id.none);
        exitButton = findViewById(R.id.exit_button);
        deleteButton=findViewById(R.id.delete_button);

        //数据库初始化
        db = Room.databaseBuilder( getApplicationContext(), DataBase.class, "mydb" )
                .allowMainThreadQueries()
                .build();
        userDao= db.userDao();

        receivedUsername = getIntent().getStringExtra("Username");

        settingAccountTextView.setText(receivedUsername);

        myListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long currentTime = System.currentTimeMillis();

                if (clickCount == 0 || (currentTime - startTime) < 3000) {
                    clickCount++;
                    startTime = currentTime;

                    if (clickCount == 3) {
                        // 在3秒内点击三次，执行跳转操作
                        Intent intent = new Intent(PersonActivity.this, AboutUsActivity.class);
                        startActivity(intent);
                    }
                } else {
                    clickCount = 1;
                    startTime = currentTime;
                }
            }
        });

        modifyPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonActivity.this, ForgotInfoActivity.class);
                startActivity(intent);
            }
        });
        noneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonActivity.this, VersionActivity.class);
                startActivity(intent);
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToLogin();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteToLogin();
            }
        });

    }

    private void ToLogin(){
        new AlertDialog.Builder(this)
                .setTitle("确认退出")
                .setMessage("您确定要退出应用吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户点击确认退出，执行退出操作

                        Intent intent = new Intent(PersonActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Intent broadcastIntent = new Intent("finish_activity");
                        sendBroadcast(broadcastIntent);
                        finish();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户点击取消，关闭对话框
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void DeleteToLogin(){
        new AlertDialog.Builder(this)
                .setTitle("确认注销")
                .setMessage("您确定要注销账户吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户点击确认退出，执行退出操作

                        User user=userDao.findUserName(receivedUsername);
                        if (user!=null) {
                            userDao.delete(user);
                            Toast.makeText(PersonActivity.this, "注销成功", Toast.LENGTH_SHORT).show();

                            finish();
                        } else {
                            Toast.makeText(PersonActivity.this, "注销失败", Toast.LENGTH_SHORT).show();
                        }

                        Intent intent = new Intent(PersonActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Intent broadcastIntent = new Intent("finish_activity");
                        sendBroadcast(broadcastIntent);
                        finish();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户点击取消，关闭对话框
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}