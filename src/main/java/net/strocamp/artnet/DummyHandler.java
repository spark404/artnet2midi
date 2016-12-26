package net.strocamp.artnet;

import net.strocamp.core.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

public class DummyHandler extends DmxHandler {
    private final static Logger logger = LoggerFactory.getLogger(DummyHandler.class);

    public DummyHandler(String name, int universe, int address, int width) {
        super(name, universe, address, width);
    }

    @Override
    public void onDmx(byte[] data) {
        logger.debug("Reveived : {}", Util.bytesToHex(data));
    }
}
