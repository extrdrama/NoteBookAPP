package com.example.tetris.others;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tetris.R;

public class VersionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);


        // 获取PackageManager
        PackageManager packageManager = getPackageManager();
        try {
            // 获取应用的PackageInfo对象
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);

            // 获取版本号和版本名
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;

            // 在TextView中显示版本信息
            TextView versionTextView = findViewById(R.id.versionTextView);
            versionTextView.setText("Version Code: " + versionCode + "\nVersion Name: " + versionName+"\n（大概不会更新）");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
