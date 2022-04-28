package com.inguana.vocabularypractice.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.inguana.vocabularypractice.Room.Migrations.Migration1To2;

@Database(entities = {Word.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static final Migration1To2 migration1To2 = new Migration1To2(1,2);

    public abstract WordDao wordDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "Sample.db")
                            .addMigrations(migration1To2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

