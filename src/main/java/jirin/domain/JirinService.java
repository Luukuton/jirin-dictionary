package jirin.domain;

import org.jsoup.HttpStatusException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class JirinService {

    private String exception;

    /**
     * queries the word from goo dictionary.
     *
     * @return return results as DictEntry object or null if unsuccessful.
     */

    public DictEntry queryDict(String input) {
        String url = "https://dictionary.goo.ne.jp/word/";
        exception = "Unkown error.";

        try {
            DictParse parser = new DictParse(url + input);
            return new DictEntry(parser.parseWord(), parser.parseReading(), parser.parseMeaning());
        }
        catch (UnknownHostException e) {
            exception = "Please check your internet connection.";
        }
        catch (SocketTimeoutException e) {
            exception = "Server took too long to respond.";
        }
        catch (HttpStatusException e) {
            exception = "Word not found.";
        }
        catch (IOException e) {
            exception = "Unknown error.";
        }
        return null;
    }

    public String getException() {
        return exception;
    }
}
