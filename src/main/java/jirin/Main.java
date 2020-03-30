package jirin;

import jirin.domain.DictParse;
import org.jsoup.HttpStatusException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        String url = "https://dictionary.goo.ne.jp/word/";
        Scanner scanner = new Scanner(System.in);
        DictParse parser;

        while (true) {
            System.out.println();
            System.out.println("Example words to try: 猫, 想像 and 聞く.");
            System.out.println("As in 'cat', 'imagination' and 'to hear'.");
            System.out.println("Typing 'exit' exits the program.");
            System.out.println();
            System.out.print("Word to search: ");

            String word = scanner.nextLine();

            if (word.equals("exit")) {
                return;
            }

            try {
                parser = new DictParse(url + word);
            }
            catch (UnknownHostException e) {
                System.out.println("Please check your internet connection.");
                continue;
            }
            catch (SocketTimeoutException e) {
                System.out.println("Server took too long to respond.");
                continue;
            }
            catch (HttpStatusException e) {
                System.out.println("Word not found.");
                continue;
            }

            System.out.println();
            System.out.println("Word: " + parser.parseWord() + " | Reading: " + parser.parseReading());
            System.out.println("Meanings: ");
            for (String m : parser.parseMeaning()) {
                System.out.println(m);
            }
        }


    }
}
