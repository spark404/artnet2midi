package net.strocamp.game;

import net.strocamp.artnet.DmxHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;

public class ButtonDmxHandler extends DmxHandler {
    private final static Logger logger = LoggerFactory.getLogger(ButtonDmxHandler.class);

    private AtomicInteger resetValue = new AtomicInteger(0);

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
        int currentValue = data[0] & 0xff;
        int previousValue = resetValue.getAndSet(currentValue);

         if (previousValue < 200 && currentValue >=200) {
            // Trigger the reset on the rising edge
            gameRunner.reset();
        }
    }


}
