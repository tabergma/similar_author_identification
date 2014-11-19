package de.hpi.smm.features;


public class WordLengthFeature {

    Float wordCount = 0.f;
    Float wordLength = 0.f;

    public void addWordCount(int count) {
        this.wordCount += count;
    }

    public void addWordLength(int wordLength) {
        this.wordLength += wordLength;
    }

    public Float getAvgWordLength() {
        return this.wordLength / this.wordCount;
    }
}
