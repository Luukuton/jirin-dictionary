package jirin.dao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A class for the handling of the settings.properties file.
 */

public class Settings {
    private final Properties settings;
    private final String settingsFile;
    private String contentFont, searchFont, theme;

    public Settings(String filename) throws IOException {
        settingsFile = filename;

        FileInputStream in = new FileInputStream(settingsFile);
        settings = new Properties();
        settings.load(in);

        theme = settings.getProperty("theme");
        searchFont = settings.getProperty("searchFont");
        contentFont = settings.getProperty("contentFont");
    }

    /**
     * Saves application settings to the settings file.
     */

    public void saveSettings(String searchFont, String contentFont, String theme) throws IOException {
        this.contentFont = contentFont;
        this.searchFont = searchFont;
        this.theme = theme;

        FileOutputStream out = new FileOutputStream(settingsFile);
        settings.setProperty("theme", theme);
        settings.setProperty("searchFont", searchFont);
        settings.setProperty("contentFont", contentFont);
        settings.store(out, null);
        out.close();
    }

    public String getTheme() {
        return theme;
    }

    public String getContentFont() {
        return contentFont;
    }

    public String getSearchFont() {
        return searchFont;
    }
}
