package com.example.tetris.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tetris.entity.User;

import java.util.List;

/*操作类*/

@Dao
public interface UserDao {
    @Query("SELECT * FROM User")
    List<User> getAll(); // 查询所有
    @Query("SELECT * FROM User WHERE username = :username AND password = :password")
    User findUser( String username, String password ); // 条件查询
    @Query("SELECT * FROM User WHERE username = :username")
    User findUserName( String username); // 用户查询
    @Insert
    void insert(User user); // 新增，由于主键自增，user不用设置uid值
    @Delete
    void delete(User user); // 单个删除
    @Update
    void update(User user); // 单个更新
    @Query("DELETE FROM User")
    void deleteAll(); // 删除所有（批量删除）
}
