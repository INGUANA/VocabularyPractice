package com.inguana.vocabularypractice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Vocabulary {

    private List<String> vocabularyWords;

    public Vocabulary() {
        vocabularyWords = new ArrayList<String>();
    }

    public Vocabulary(String instanceLocalFilePath) {
        vocabularyWords = new ArrayList<String>();
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
        return vocabularyWords.get((int) (Math.random()*(vocabularyWords.size() + 1)));
    }

    public void removeWord(String wordToRemove) {
        vocabularyWords.remove(wordToRemove);
    }
}
