package com.example.tetris.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int uid; //用户id
    @ColumnInfo(name = "username")
    public String username; //用户名
    @ColumnInfo(name = "password")
    public String password; //用户密码
}
