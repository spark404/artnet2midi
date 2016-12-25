package net.strocamp.game;

import net.strocamp.titan.TitanDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class GameRunnerImpl implements GameRunner {
    private final static Logger logger = LoggerFactory.getLogger(GameRunnerImpl.class);

    private PiInterface piInterface;
    private TitanDispatcher titanDispatcher;

    private final AtomicBoolean buttonPressed = new AtomicBoolean(false);

    private Button activeButton;
    private Map<Button, Integer> playbacks;

    private int buttonAPlayback;
    private int buttonBPlayback;

    @Autowired
    public void setPiInterface(PiInterface piInterface) {
        this.piInterface = piInterface;
    }

    @Autowired
    public void setTitanDispatcher(TitanDispatcher titanDispatcher) {
        this.titanDispatcher = titanDispatcher;
    }

    @Value("${gamerunner.button_a.playback}")
    public void setButtonAPlayback(int buttonAPlayback) {
        this.buttonAPlayback = buttonAPlayback;
    }

    @Value("${gamerunner.button_b.playback}")
    public void setButtonBPlayback(int buttonBPlayback) {
        this.buttonBPlayback = buttonBPlayback;
    }

    @PostConstruct
    public void initialize() {
        piInterface.onAButton(() -> buttonPress(Button.BUTTON_A));
        piInterface.onBButton(() -> buttonPress(Button.BUTTON_B));
        piInterface.onResetButton(this::reset);

        this.playbacks = new HashMap<>();
        playbacks.put(Button.BUTTON_A, buttonAPlayback);
        playbacks.put(Button.BUTTON_B, buttonBPlayback);

        logger.info("Raspberry GPIO Initialized");
    }

    @Override
    @Async
    public void reset() {
        buttonPressed.set(false);
        piInterface.ledOff();
        try {
            if (activeButton != null) {
                titanDispatcher.firePlayback(playbacks.get(activeButton), 0, true);
                activeButton = null;
            }
        } catch (Exception e) {
            logger.error("Failed to fire trigger on the Titan", e);
        }
    }

    @Override
    @Async
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
            activeButton = button;
            titanDispatcher.firePlayback(playbacks.get(button), 1, true);
        } catch (Exception e) {
            logger.error("Failed to fire trigger on the Titan", e);
        }
    }

    enum Button {
        BUTTON_A,
        BUTTON_B;
    }

}
