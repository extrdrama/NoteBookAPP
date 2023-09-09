package com.example.tetris.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tetris.entity.UsersDay;

import java.util.List;

@Dao
public interface UsersDayDao {
    @Insert
    void insertDiary(UsersDay usersDay);

    @Update
    void updateDiary(UsersDay usersDay);

    @Delete
    void deleteDiary(UsersDay usersDay);

    @Query("SELECT * FROM usersDay WHERE username = :username AND createTime = :createTime")
    UsersDay getDiaryByUsernameAndTime(String username, String createTime);

    //查询用户所有日记
    @Query("SELECT * FROM usersDay WHERE username = :username ORDER BY createTime DESC")
    List<UsersDay> getAllDiariesForUser(String username);

// || 运算符来匹配日期。% 通配符表示匹配所有时间
    @Query("SELECT * FROM usersDay WHERE username = :username AND createTime LIKE :selectedDate || '%'")
    List<UsersDay> getDiariesForSelectedDate(String username, String selectedDate);


}
