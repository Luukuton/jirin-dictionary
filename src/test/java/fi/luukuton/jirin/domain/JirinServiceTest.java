package fi.luukuton.jirin.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JirinServiceTest {

    JirinService jirin;
    String mode;

    @Before
    public void setUp() {
        jirin = new JirinService();
        mode = "m1u";
    }

    // This search term (読む) returns only one result.
    @Test
    public void returnsCorrectLinksOnSingleResult() {
        assertEquals(jirin.queryDict("読む", mode).get(0).getWord(), "読む");
    }

    // This search term (鳥) returns multiple results.
    @Test
    public void returnsCorrectLinksOnMultipleResults() {
        assertEquals(jirin.queryDict("鳥", mode).get(0).getWord(), "鳥");
    }

    @Test
    public void returnsNullWhenZeroLengthInput() {
        assertNull(jirin.queryDict("", mode));
    }

    @Test
    public void equalWhenReturnsCorrectErrorForZeroLengthInput() {
        jirin.queryDict("", mode);
        assertEquals("Search term cannot be nothing.", jirin.getException());
    }

    @Test
    public void returnsNullWhenWordNotFound() {
        assertNull(jirin.queryDict("-", mode));
    }

    @Test
    public void equalWhenReturnsCorrectExceptionForWordNotFound() {
        jirin.queryDict("-", mode);
        assertEquals("No results.", jirin.getException());
    }
}
