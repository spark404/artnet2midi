package net.strocamp.game;

import net.strocamp.artnet.DmxHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;

public class ButtonDmxHandler extends DmxHandler {
    private final static Logger logger = LoggerFactory.getLogger(ButtonDmxHandler.class);

    AtomicInteger resetValue = new AtomicInteger(0);

    private GameRunner gameRunner;

    @Autowired
    public void setGameRunner(GameRunner gameRunner) {
        this.gameRunner = gameRunner;
    }

    public ButtonDmxHandler(String name, int universe, int address) {
        super(name, universe, address, 2);
    }

    @Override
    public void onDmx(byte[] data) {
        logger.info("Received DMX data for this button {}", String.format("%x %x", data[0], data[1]));

        int currentValue = data[0] & 0xff;
        int comparison = resetValue.getAndSet(currentValue);

        if (comparison >= 200 && currentValue < 200) {
            // Falling edge
            logger.info("Falling Edge");
        } else if (comparison < 200 && currentValue >=200) {
            // rising edge
            logger.info("Rising Edge");
            gameRunner.reset();
        }
    }


}
