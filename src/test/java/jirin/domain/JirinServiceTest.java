package jirin.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JirinServiceTest {

    JirinService jirin;

    @Before
    public void setUp() {
        jirin = new JirinService();
    }

    @Test
    public void returnsNullWhenZeroLengthInput() {
        assertNull(jirin.queryDict(""));
    }

    @Test
    public void equalWhenReturnsCorrectErrorForZeroLengthInput() {
        jirin.queryDict("");
        assertEquals("Search term cannot be nothing.", jirin.getException());
    }

    @Test
    public void returnsNullWhenWordNotFound() {
        assertNull(jirin.queryDict("-"));
    }

    @Test
    public void equalWhenReturnsCorrectExceptionForWordNotFound() {
        jirin.queryDict("-");
        assertEquals("Word not found.", jirin.getException());
    }
}