package com.inguana.vocabularypractice.Room.Migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration1To2 extends Migration {

    public Migration1To2(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        // 1. Create new table
        database.execSQL("CREATE TABLE IF NOT EXISTS `word_temp_table` " +
                "(`word` TEXT NOT NULL, " +
                "`module_name` TEXT NOT NULL, " +
                "PRIMARY KEY(`word`, `module_name`))");

        // 2. Copy the data
        database.execSQL("INSERT INTO `word_temp_table` (`word`, `module_name`) "
                + "SELECT `word`, `module_name` "
                + "FROM `Word`");

        // 3. Remove the old table
        database.execSQL("DROP TABLE `Word`");

        // 4. Change the table name to the correct one
        database.execSQL("ALTER TABLE `word_temp_table` RENAME TO `Word`");
    }
}
