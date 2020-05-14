package com.inguana.vocabularypractice.rest.response;

import java.util.List;

public class BaseResponse {

    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data {
        private String slug;
        private List<Japanese> japanese;
        private List<Senses> senses;


        public class Senses {

            private List<String> english_definitions;
            private List<String> parts_of_speech;
        }
        public class Japanese {
            private String word;
            private String reading;

            public String getWord() {
                return word;
            }

            public String getReading() {
                return reading;
            }

            public void setWord(String word) {
                this.word = word;
            }

            public void setReading(String reading) {
                this.reading = reading;
            }

        }
        public String getSlug() {
            return slug;
        }

        public List<Japanese> getJapanese() {
            return japanese;
        }

        public List<Senses> getSenses() {
            return senses;
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
