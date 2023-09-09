package com.example.tetris.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Dao;
import androidx.room.Room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tetris.R;
import com.example.tetris.adapt.DayAdapter;
import com.example.tetris.adapt.DayItem;
import com.example.tetris.adapt.ListAdapter;
import com.example.tetris.adapt.ListItem;
import com.example.tetris.dao.UsersDayDao;
import com.example.tetris.dao.UsersListDao;
import com.example.tetris.db.DataBase;
import com.example.tetris.entity.UsersDay;
import com.example.tetris.entity.UsersList;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ImageView circularImageButton;
    private TextView centerTextView;
    private ImageView rightTopButton;
    private ListView listView;
    private ImageView leftCircleImageView;
    private TextView username;
    private DataBase db;
    private UsersListDao usersListDao;
    private String receivedUsername;
    private List<UsersList> usersLists;
    private ArrayList<ListItem> itemList;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        init();
    }
    private void init(){
        circularImageButton = findViewById(R.id.circular_image_button);
        centerTextView = findViewById(R.id.center_text);
        rightTopButton = findViewById(R.id.right_top_button);
        listView = findViewById(R.id.list_view);
        leftCircleImageView = findViewById(R.id.imageView);
        username=findViewById(R.id.textView3);

        //数据库初始化
        db = Room.databaseBuilder(getApplicationContext(), DataBase.class, "mydb")
//                       .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        usersListDao = db.usersListDao();
        itemList = new ArrayList<>();
        usersLists=new ArrayList<>();

        // 获取传递过来的Username
        receivedUsername = getIntent().getStringExtra("Username");
        // 将Username设置到TextView中
        username.setText(receivedUsername);

        usersLists=usersListDao.getAllDiariesForUser(receivedUsername);
        if(itemList!=null) itemList.clear();
        for (UsersList usl:usersLists
             ) {

            itemList.add(new ListItem(usl.getContent(),usl.getCreateTime(),usl.getUsername()));
        }
        adapter = new ListAdapter(ListActivity.this, itemList);
        listView.setAdapter(adapter);



        leftCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            finish();

            }
        });

        rightTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ListActivity.this, CreateListActivity.class);
                intent.putExtra("Username", receivedUsername);
                startActivity(intent);
            }
        });

        circularImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ListActivity.this, PersonActivity.class);
                intent.putExtra("Username", receivedUsername);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();

        initListView();
    }

    private void initListView() {
        usersLists=usersListDao.getAllDiariesForUser(receivedUsername);
        if(itemList!=null) itemList.clear();
        for (UsersList usl:usersLists
        ) {

            itemList.add(new ListItem(usl.getContent(),usl.getCreateTime(),usl.getUsername()));
        }
        adapter = new ListAdapter(ListActivity.this, itemList);
        listView.setAdapter(adapter);
    }
}