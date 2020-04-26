package jirin.domain;

import org.jsoup.HttpStatusException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * A class for getting results of an online Japanese dictionary.
 */

public class JirinService {

    private String exception;

    /**
     * Queries the word from the dictionary with the help of the DictParse.
     *
     * @return return results as DictEntry object or null if unsuccessful.
     */

    public DictEntry queryDict(String input) {
        String mode = "/m0u/";
        exception = "Search term cannot be nothing.";

        if (input.length() == 0) {
            return null;
        }

        try {
            DictParse parser = new DictParse(input + mode);
            return new DictEntry(parser.parseWord(), parser.parseReading(), parser.parseMeaning());
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

    public String getException() {
        return exception;
    }
}
