package com.inguana.vocabularypractice.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordDao {

    @Query("SELECT * FROM Word")
    List<Word> getAll();

    @Query("SELECT * FROM Word WHERE module_name = :module_name")
    List<Word> getModule(String module_name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllModule(List<Word> newModule);//... means zero or more Word objects are eligible to pass as parameters

    @Delete
    void delete(List<Word> module);
}
