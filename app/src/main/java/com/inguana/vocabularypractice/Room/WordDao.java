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

    @Query("SELECT DISTINCT module_name FROM Word")
    List<String> getAllModules();

    @Query("SELECT " +
            "CASE WHEN EXISTS ( " +
            "SELECT DISTINCT module_name FROM Word WHERE module_name = :module_name " +
            ") " +
            "THEN 1 " +
            "ELSE 0 " +
            "END;")
    boolean checkIfDuplicate(String module_name);

    @Query("SELECT " +
            "CASE WHEN EXISTS ( " +
            "SELECT DISTINCT module_name FROM Word WHERE module_name = :module_name " +
            "EXCEPT " +
            "SELECT DISTINCT module_name FROM Word WHERE module_name = :except_module_name" +
            ") " +
            "THEN 1 " +
            "ELSE 0 " +
            "END;")
    boolean checkIfDuplicateExcept(String module_name, String except_module_name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllModule(List<Word> newModule);//... means zero or more Word objects are eligible to pass as parameters

    @Delete
    void delete(List<Word> module);

    @Query("DELETE FROM Word WHERE module_name = :module_name")
    void deleteByModuleName(String module_name);
}
