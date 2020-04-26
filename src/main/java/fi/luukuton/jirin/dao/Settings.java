package fi.luukuton.jirin.dao;

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
     *
     * @param searchFont name of the font used in the search bar
     * @param contentFont name of the font used in the results and titles
     * @param theme theme of the application (Light or Dark)
     * @throws IOException possibly when the file is corrupted or deleted by a third party
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
