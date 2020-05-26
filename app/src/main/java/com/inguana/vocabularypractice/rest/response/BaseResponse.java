package com.inguana.vocabularypractice.rest.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.inguana.vocabularypractice.Utilities.Validations.isNullOrEmpty;

public class BaseResponse {

    private List<Data> data;

    public List<Data> getData() {
        return isNullOrEmpty(data) ? Collections.singletonList(new Data()) : data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data {
        private String slug;
        private List<Japanese> japanese;
        private List<Senses> senses;

        public Data() {
            this.slug = "";
            this.japanese = Collections.singletonList(new Japanese());
            this.senses = Collections.singletonList(new Senses());
        }

        public class Senses {

            private List<String> english_definitions;
            private List<String> parts_of_speech;
        }
        public class Japanese {
            private String word;
            private String reading;

            public Japanese() {
                this.word = "";
                this.reading = "";
            }

            public String getWord() {
                return isNullOrEmpty(word) ? "" : word;
            }

            public String getReading() {
                return isNullOrEmpty(reading) ? "" : reading;
            }

            public void setWord(String word) {
                this.word = word;
            }

            public void setReading(String reading) {
                this.reading = reading;
            }

        }
        public String getSlug() {
            return isNullOrEmpty(slug) ? "" : slug;
        }

        public List<Japanese> getJapanese() {
            return isNullOrEmpty(japanese) ? Collections.singletonList(new Japanese()) : japanese;
        }

        public List<Senses> getSenses() {
            return isNullOrEmpty(senses) ? new ArrayList<>() : senses;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public void setJapanese(List<Japanese> japanese) {
            this.japanese = japanese;
        }

        public void setSenses(List<Senses> senses) {
            this.senses = senses;
        }
    }
}
