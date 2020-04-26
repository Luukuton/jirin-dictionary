package fi.luukuton.jirin.dao;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class SettingsTest {
    @Test
    public void equalWhenReadingSettingsFileIsSuccessful() throws IOException {
        File file = new File("src/test/resources/settingsTest.properties");
        Settings settings = new Settings(file.getAbsolutePath());
        assertEquals("dark", settings.getTheme());
        assertEquals("M Plus 1p", settings.getSearchFont());
        assertEquals("M Plus 1p", settings.getContentFont());
    }
}
