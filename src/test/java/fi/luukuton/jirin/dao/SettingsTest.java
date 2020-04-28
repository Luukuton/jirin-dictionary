package fi.luukuton.jirin.dao;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class SettingsTest {

    Settings settings;

    @Before
    public void setUp() throws IOException {
        File file = new File("src/test/resources/settingsTest.properties");
        settings = new Settings(file.getAbsolutePath());
    }

    @Test
    public void equalWhenReadingSettingsFileIsSuccessful() {
        assertEquals("dark", settings.getTheme());
        assertEquals("M Plus 1p", settings.getSearchFont());
        assertEquals("M Plus 1p", settings.getContentFont());
    }

    @Test
    public void equalWhenReadingAfterWritingSettingsFileIsSuccessful() throws IOException {
        settings.saveSettings("M Plus 1p", "M Plus 1p", "light");
        assertEquals("light", settings.getTheme());
        assertEquals("M Plus 1p", settings.getSearchFont());
        assertEquals("M Plus 1p", settings.getContentFont());
        settings.saveSettings("M Plus 1p", "M Plus 1p", "dark");
    }
}
