package net.strocamp.game;

import net.strocamp.titan.TitanDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class GameRunner {
    private final static Logger logger = LoggerFactory.getLogger(GameRunner.class);

    private PiInterface piInterface;
    private TitanDispatcher titanDispatcher;

    final AtomicBoolean buttonPressed = new AtomicBoolean(false);

    @Autowired
    public void setPiInterface(PiInterface piInterface) {
        this.piInterface = piInterface;
    }

    @Autowired
    public void setTitanDispatcher(TitanDispatcher titanDispatcher) {
        this.titanDispatcher = titanDispatcher;
    }

    @PostConstruct
    public void initialize() {
        piInterface.onAButton(() -> buttonPress(Button.BUTTON_A));
        piInterface.onBButton(() -> buttonPress(Button.BUTTON_B));
        piInterface.onResetButton(() -> reset());
        logger.info("Raspberry GPIO Initialized");
    }

    public void reset() {
        buttonPressed.set(false);
        piInterface.ledOff();
        try {
            titanDispatcher.firePlayback(7, 0, true);
        } catch (Exception e) {
            logger.error("Failed to fire trigger on the Titan", e);
        }
    }

    public void buttonPress(Button button) {
        boolean result = buttonPressed.compareAndSet(false, true);
        if (result) {
            // You are the one
            piInterface.ledOn();
            triggerCue(button);
        }
    }

    private void triggerCue(Button button) {
        try {
            titanDispatcher.firePlayback(7, 1, true);
        } catch (Exception e) {
            logger.error("Failed to fire trigger on the Titan", e);
        }
    }

    private enum Button {
        BUTTON_A,
        BUTTON_B;
    }

}
