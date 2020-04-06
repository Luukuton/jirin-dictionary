package jirin.domain;

import java.util.ArrayList;

/**
 * A class for a dictionary entry for one word.
 */

public class DictEntry {
    private String word, reading;
    private ArrayList<String> meanings;

    public DictEntry(String word, String reading, ArrayList<String> meanings) {
        this.word = word;
        this.reading = reading;
        this.meanings = meanings;
    }

    public String getWord() {
        return word;
    }

    public String getReading() {
        return reading;
    }

    public ArrayList<String> getMeanings() {
        return meanings;
    }
}
