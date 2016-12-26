package net.strocamp.artnet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class DmxHandlerTest {
    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void testAddress() throws Exception {
        new DmxHandler("Test", 0, 512, 1) {
            @Override
            public void onDmx(byte[] data) {
                // Nothing
            }
        };
    }

    @Test
    public void testInvalidAddress() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Address");

        new DmxHandler("Test", 0, 0, 15) {
            @Override
            public void onDmx(byte[] data) {
                // Nothing
            }
        };
    }

    @Test
    public void testInvalidWidth() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Width should be valid");

        new DmxHandler("Test", 0, 512, 2) {
            @Override
            public void onDmx(byte[] data) {
                // Nothing
            }
        };
    }

}