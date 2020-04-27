package fi.luukuton.jirin;

import fi.luukuton.jirin.dao.Settings;
import fi.luukuton.jirin.ui.JirinUI;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // Japanese Input (Mozc) doesn't work on Linux with GTK3, so GTK2 is necessary.
        System.setProperty("jdk.gtk.version", "2");

        File file = new File("settings.properties");
        if (file.createNewFile()) {
            Settings settings = new Settings("settings.properties");
            settings.saveSettings("M Plus 1p", "M Plus 1p", "light");
        }

        JirinUI.main(args);
    }
}
