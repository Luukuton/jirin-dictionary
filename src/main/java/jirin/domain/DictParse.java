package jirin.domain;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A class for parsing a Japanese dictionary.
 */

public class DictParse {
    private final Document dictSite;

    public DictParse(String address) throws IOException {
        this.dictSite = Jsoup.connect(address).get();
    }

    /**
     * Parses the word itself in kanji form if applicable.
     *
     * @return return the word as String.
     */

    public String parseWord() {
        Element word = dictSite
                .getElementsByClass("basic_title nolink l-pgTitle").get(0)
                .getElementsByTag("h1").get(0);

        return word.html().replaceAll("<span.*の意味.*</span>", "").strip();
    }

    /**
     * Parses the reading of the word and removes Japanese parentheses.
     *
     * @return return the reading of a word as String.
     */

    public String parseReading() {
        Element reading = dictSite
                .getElementsByClass("basic_title nolink l-pgTitle").get(0)
                .getElementsByTag("h1").get(0)
                .getElementsByTag("span").get(0);

        if (reading.text().equals("の意味")) {
            return parseWord();
        } else {
            return reading.text().replaceAll("[（）]", "").strip();
        }
    }

    /**
     * Parses all meanings of the word into an ArrayList.
     *
     * @return return meanings in an array.
     */

    public ArrayList<String> parseMeaning() {
        ArrayList<String> meanings = new ArrayList<>();

        Elements meaningsRaw = dictSite.getElementsByClass("meaning cx");

        // If the word has only one meaning, the HTML structure is a little bit different.
        if (meaningsRaw.isEmpty()) {
            meaningsRaw = dictSite.getElementsByClass("content-box contents_area meaning_area p10");
        }

        for (Element meaning : meaningsRaw) {
            meanings.add(meaning.text().strip());
        }

        return meanings;
    }
}
