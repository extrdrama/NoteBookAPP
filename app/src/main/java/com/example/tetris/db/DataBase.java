package com.example.tetris.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.tetris.dao.UserDao;
import com.example.tetris.dao.UsersDayDao;
import com.example.tetris.dao.UsersListDao;
import com.example.tetris.entity.User;
import com.example.tetris.entity.UsersDay;
import com.example.tetris.entity.UsersList;

/*数据库类*/
@Database(entities = { User.class, UsersDay.class, UsersList.class}, version = 9)
public abstract class DataBase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract UsersDayDao usersDayDao();
    public abstract UsersListDao usersListDao();
}