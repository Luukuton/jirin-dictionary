package fi.luukuton.jirin.domain;

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

    public DictParse(String link) throws IOException {
        String domainURL = "https://dictionary.goo.ne.jp";
        dictSite = Jsoup.connect(domainURL + link).get();
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

        String parsed = word.html().replaceAll("<span.*の意味.*</span>", "").strip();
        return Jsoup.parse(parsed).text();
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
     * Parses all definitions of the word into an ArrayList.
     *
     * @return return definitions in an array.
     */

    public ArrayList<String> parseDefinition() {
        ArrayList<String> definitions = new ArrayList<>();

        Elements definitionsRaw = dictSite.getElementsByClass("meaning cx");

        // If the word has only one definition, the HTML structure is a little bit different.
        if (definitionsRaw.isEmpty()) {
            definitionsRaw = dictSite.getElementsByClass("content-box contents_area meaning_area p10");
        }

        for (Element definition : definitionsRaw) {
            definitions.add(definition.text().strip());
        }

        return definitions;
    }
}
