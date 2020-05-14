package com.inguana.vocabularypractice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Vocabulary {

    private List<String> vocabularyWords;
    public static final String NO_WORD_IN_LIST = "123456789";

    public Vocabulary() {
        vocabularyWords = new ArrayList<>();
    }

    public Vocabulary(List<String> vocabularyWords) {
        this.vocabularyWords = vocabularyWords;
    }


    public Vocabulary(String instanceLocalFilePath) {
        vocabularyWords = new ArrayList<>();
        File vocabularyFile = new File(instanceLocalFilePath);
        try {
            if (vocabularyFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(vocabularyFile);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line = bufferedReader.readLine();
                while (null != line) {
                    vocabularyWords.add(line);
                    line = bufferedReader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setVocabularyWords(List<String> vocabularyWords) {
        this.vocabularyWords = vocabularyWords;
    }

    public List<String> getVocabularyWords() {
        return vocabularyWords;
    }

    public String getRandomVocabularyWord() {
        String result;

        if(0 == vocabularyWords.size()) {
            result = NO_WORD_IN_LIST;
        } else {
            result = vocabularyWords.get(ThreadLocalRandom.current().nextInt(0, vocabularyWords.size())); // nextInt doesnt include last number of max, in this case it is beneficial since size is always +1 from list's last pos
        }
        return result;

        //DONE: fix this shit^^^^^
        //TODO: check delay from moduleList->WordGuess
        //DONE: when list is ending present sth instead of crashing
        //DONE: find what is going on with back on wordguess. (also this needs optimize as described in other TODO note)
    }

    public void removeWord(String wordToRemove) {
        vocabularyWords.remove(wordToRemove);
    }
}
