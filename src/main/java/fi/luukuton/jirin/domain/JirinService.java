package fi.luukuton.jirin.domain;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A class for getting results of an online Japanese dictionary.
 */

public class JirinService {
    private String exception;
    private String contentURL;

    /**
     * Queries the word from the dictionary with the help of the DictParse.
     *
     * @param input input string typed into the search bar by the user
     * @param mode search mode (forward = m0u, exact = m1u, backward = m2u)
     * @return return results as DictEntry object or null if unsuccessful.
     */

    public ArrayList<DictEntry> queryDict(String input, String mode) {
        exception = "Search term cannot be nothing.";
        contentURL = input + "/" + mode;

        if (input.length() == 0) {
            return null;
        }

        try {
            ArrayList<DictEntry> results = new ArrayList<>();
            HashSet<String> links = parseSearchResults();

            // If no results, or if the search was directly redirected to the result page.
            if (!(links == null)) {
                for (String link : links) {
                    DictParse parser = new DictParse(link);
                    results.add(new DictEntry(parser.parseWord(), parser.parseReading(), parser.parseMeaning()));
                }
            } else {
                DictParse parser = new DictParse("/word/" + input);
                results.add(new DictEntry(parser.parseWord(), parser.parseReading(), parser.parseMeaning()));
            }

            return results;
        } catch (UnknownHostException e) {
            exception = "Please check your internet connection.";
        } catch (SocketTimeoutException e) {
            exception = "Server took too long to respond.";
        } catch (HttpStatusException | IndexOutOfBoundsException e) {
            exception = "No results.";
        } catch (IOException e) {
            exception = "Unknown error.";
        }
        return null;
    }

    /**
     * Parses the search results page.
     *
     * @return return links to the results as an ArrayList<String>.
     */

    private HashSet<String> parseSearchResults() throws IOException {
        HashSet<String> links = new HashSet<>();
        ArrayList<Elements> searchResults = new ArrayList<>();
        String domainURL = "https://dictionary.goo.ne.jp";

        Document searchResultPage = Jsoup.connect(domainURL + "/srch/jn/" + contentURL).get();
        Elements queryResults = searchResultPage.getElementsByClass("content_list idiom lsize");

        if (!queryResults.isEmpty()) {
            for (Element el : queryResults) {
                searchResults.add(el.getElementsByTag("li"));
            }
        } else {
            return null;
        }

        for (Elements els : searchResults) {
            for (Element el : els) {
                // Split removes identifiers from the end of the link, which removes any possibility of duplicates.
                links.add(el.getElementsByTag("a").get(0).attr("href").split("#jn-\\d*(/|)")[0]);
            }
        }

        return links;
    }

    public String getException() {
        return exception;
    }
}
