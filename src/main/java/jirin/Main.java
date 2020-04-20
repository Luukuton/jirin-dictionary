package jirin;

import jirin.dao.Settings;
import jirin.ui.JirinUI;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("settings.properties");

        if (file.createNewFile()) {
            Settings settings = new Settings("settings.properties");
            settings.saveSettings("M Plus 1p", "M Plus 1p", "light");
        }

        JirinUI.main(args);
    }
}
