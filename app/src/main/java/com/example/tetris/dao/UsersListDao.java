package com.example.tetris.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tetris.entity.UsersDay;
import com.example.tetris.entity.UsersList;

import java.util.List;

@Dao
public interface UsersListDao {
    @Insert
    void insertList(UsersList usersList);

    @Update
    void updateList(UsersList usersList);

    @Delete
    void deleteList(UsersList usersList);

    @Query("SELECT * FROM usersList WHERE username = :username AND createTime = :createTime")
    UsersList getDiaryByUsernameAndTime(String username, String createTime);

    //查询用户所有日记
    @Query("SELECT * FROM usersList WHERE username = :username ORDER BY createTime DESC")
    List<UsersList> getAllDiariesForUser(String username);
}
