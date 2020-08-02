# VocabularyPractice

This project aims to replicate the vocal/memory process of learning new words of a foreign language. By showing words in one language, the user guesses the translation, and once he does, he can view it to see if he was right. ***BETA** Version supports only **English to Japanese**, in future releases there will be more options.*

## Use of the App:

As of now there are two basic flows for learning.
##### 1. Train with random words.
This is quite direct. As soon as the app starts, the user presses the "Start" button to begin the training, in which he will face one of the 10000 most used words according to Google. *Note that there are a few weird words/abbreviations in there. Words were pulled from [this](https://github.com/first20hours/google-10000-english) repository.*

##### 2. Train with user words.
Apart from random words, the user can create modules in which they write their own words and train on them. All it takes is to type the word that will get translated and by starting training with the newly created module, the app will translate those words to the selected language.

There is some additional functionality built in to add flexibility towards the user's needs. Features will be added in the future.

## About the future of Vocabulary Practice

I built this app to help people who learn Japanese and also apply my knowledge of Android to one of my own projects. This is a starting point. Going forward I'm going to expand Vocabulary Practice in two dimensions;

- **Optimization**
  - Right now the app doesn't have the cleanest architecture, nor the best code practices applied. It is made with MVC and some basic knowledge from my part that i got by working on Android. What I want is to use this project to apply newly learned knowledge about Android practices and coding in general. After a good cleanup i plan to move on either MVP->MVVM or MVVM architecture. At some point i will swap to Kotlin aswell. Android has a vast range of tools that either I'm not aware of or I simply haven't used yet, so I'm taking this chance to test and experiment on whatever is being widely used/is more optimal/is interesting.
- **New Features**
  - Apart from the optimization as defined above, i really want to make this app as practical as possible for the potential user. There are some features that will work on once the realease to the PlayStore is being made, like the selection of language from source word to translation word, or adding more information on the translated word.
  
*Because this app is for learning purposes as well, I'm keeping it Public and I'm happy to view any kind of suggestion, from a variable naming pattern to refactoring parts of the app. Of course that doesn't excude proposing any new features that may be useful for the user.*
