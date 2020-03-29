package com.inguana.vocabularypractice.Room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Word {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "word")
    public String word;

    @ColumnInfo(name = "module_name")
    public String module_name;

    public Word(String word, String module_name) {
        this.word = word;
        this.module_name = module_name;
    }

    public String getWord() {
        return word;
    }

    public String getModule_name() {
        return module_name;
    }
}
