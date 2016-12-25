package net.strocamp.game;

import org.springframework.scheduling.annotation.Async;

/**
 * Created by hugo on 25/12/2016.
 */
public interface GameRunner {
    @Async
    void reset();

    @Async
    void buttonPress(GameRunnerImpl.Button button);
}
