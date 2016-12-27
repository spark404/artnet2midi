package net.strocamp.titan;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore
public class TitanDispatcherTest {

    private TitanDispatcherImpl titanDispatcher;

    @Before
    public void setUp() throws Exception {
        titanDispatcher = new TitanDispatcherImpl();
        titanDispatcher.setBaseUrl("http://192.168.168.107:4430");
    }

    @Test
    public void getVersion() throws Exception {
        String actual = titanDispatcher.getVersion();

        assertEquals("\"10.0\"", actual);
    }

    @Test
    public void getShowName() throws Exception {
        String actual = titanDispatcher.getShowName();

        assertEquals("\"Test Show Hugo\"", actual);

    }
    @Test
    public void getHandles() throws Exception {
        titanDispatcher.firePlayback(7,0.888f,true);
        Thread.sleep(1000);
        titanDispatcher.firePlayback(7,0,true);
    }

}