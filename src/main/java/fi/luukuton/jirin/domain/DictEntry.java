package fi.luukuton.jirin.domain;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * A class for a dictionary entry for one word.
 */

public class DictEntry {
    private String url;
    private final String word, reading;
    private final ArrayList<String> definitions;

    public DictEntry(String word, String reading, ArrayList<String> definitions) {
        this.word = word;
        this.reading = reading;
        this.definitions = definitions;
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

    public ArrayList<String> getDefinitions() {
        return definitions;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }
}
