package net.strocamp.game;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ButtonDmxHandlerTest {
    @Test
    public void onDmx() throws Exception {
        ButtonDmxHandler dmxHandler = new ButtonDmxHandler("Test", 0, 0);
        GameRunner gameRunner = mock(GameRunner.class);
        dmxHandler.setGameRunner(gameRunner);

        dmxHandler.onDmx(new byte[] { (byte)0xff, 0x0 });
        dmxHandler.onDmx(new byte[] { (byte)0xff, 0x0 });
        dmxHandler.onDmx(new byte[] { (byte)0x44, 0x0 });

        verify(gameRunner, times(1)).reset();
    }

}