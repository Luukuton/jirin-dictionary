package jirin.domain;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * A class for a dictionary entry for one word.
 */

public class DictEntry {
    private final String word, reading;
    private final ArrayList<String> meanings;

    public DictEntry(String word, String reading, ArrayList<String> meanings) {
        this.word = word;
        this.reading = reading;
        this.meanings = meanings;
    }

    /**
     * Encodes the word in UTF-8. Used in URLs.
     *
     * @return return the word encoded in UTF-8
     */

    public String hexEncodeWord() {
        return URLEncoder.encode(word, StandardCharsets.UTF_8);
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
