package com.inguana.vocabularypractice.Utilities;

import com.inguana.vocabularypractice.Room.Word;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class Validations {

    public static boolean isNullOrEmpty(String object) {
        return isNull(object) || isEmpty(object);
    }

    public static boolean isNullOrEmpty(Collection<?> object) {
        return isNull(object) || isEmpty(object);
    }

    public static boolean isEmpty(String string) {
        return string.isEmpty();
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection.isEmpty();
    }

    /*public List<Word> getCleanWordList(String moduleName) {//TODO: check with empty list
        return wordList.stream()
                .limit(wordList.size() - 1)
                .filter(item -> !item.isEmpty())
                .map(item -> new Word(item, moduleName))
                .collect(Collectors.toList());
    }*/
}
