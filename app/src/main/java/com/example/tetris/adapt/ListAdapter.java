package com.example.tetris.adapt;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.room.Room;

import com.example.tetris.R;
import com.example.tetris.dao.UsersDayDao;
import com.example.tetris.dao.UsersListDao;
import com.example.tetris.db.DataBase;
import com.example.tetris.entity.UsersDay;
import com.example.tetris.entity.UsersList;
import com.example.tetris.menu.Edit_dayActivity;
import com.example.tetris.menu.Edit_listActivity;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ListItem> itemList;
    private String username;
    private DataBase db;
    private UsersListDao usersListDao;

    
    public ListAdapter(Context context, ArrayList<ListItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_list_item, parent, false);
        }

        //数据库初始化
        db = Room.databaseBuilder(context, DataBase.class, "mydb")
//                       .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        usersListDao = db.usersListDao();
        
        // 获取列表项中的各个视图

        TextView descriptionTextView = convertView.findViewById(R.id.item_description);
        TextView dateTextView = convertView.findViewById(R.id.item_date);
        ImageButton button1 = convertView.findViewById(R.id.button1);
        ImageButton button2 = convertView.findViewById(R.id.button2);

        // 获取当前位置的数据项
        ListItem currentItem = itemList.get(position);

        // 设置视图中的数据
        String str= currentItem.getDescription();
        if (str!=null) {
            descriptionTextView.setText(str.substring(0, Math.min(str.length(), 10)));
        }
        else {
            str="随便写点什么吧";
            descriptionTextView.setText(str.substring(0, Math.min(str.length(), 10)));
        }
        dateTextView.setText(currentItem.getDate());

        username=itemList.get(0).getUsername();
        // 设置按钮的点击事件
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Edit_listActivity.class);
                intent.putExtra("Date", currentItem.getDate());
                intent.putExtra("Username", currentItem.getUsername());
                context.startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersList usersList=usersListDao.getDiaryByUsernameAndTime(currentItem.getUsername(),currentItem.getDate());
                usersListDao.deleteList(usersList);
                ArrayList<ListItem>ListItems=new ArrayList<>();
                List<UsersList> usersLists = usersListDao.getAllDiariesForUser(currentItem.getUsername());
                for (UsersList usl:usersLists
                ) {
                    ListItems.add(new ListItem(usl.getContent(),usl.getCreateTime(),usl.getUsername()));
                }
                updateData(ListItems);
            }
        });

        return convertView;
    }
    public void updateData(ArrayList<ListItem> updatedItemList) {
        itemList.clear();
        itemList.addAll(updatedItemList);
        notifyDataSetChanged();
    }
}
