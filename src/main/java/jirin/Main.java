package jirin;

import jirin.domain.DictParse;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        String url = "https://dictionary.goo.ne.jp/word/";
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Example words to try: 猫, 想像 and 聞く.");
            System.out.println("As in 'cat', 'imagination' and 'to hear'.");
            System.out.println("Typing 'exit' exits the program.");
            System.out.println();
            System.out.print("Word to search: ");

            String word = scanner.nextLine();

            if (word.equals("exit")) {
                return;
            }

            DictParse parser = new DictParse(url + word);

            System.out.println();
            System.out.println("Word: " + parser.parseWord() + " | Reading: " + parser.parseReading());
            System.out.println();
            System.out.println("Meanings: ");
            for (String m : parser.parseMeaning()) {
                System.out.println(m);
            }
        }


    }
}
