package jirin.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        assertEquals("No results.", jirin.getException());
    }
}
