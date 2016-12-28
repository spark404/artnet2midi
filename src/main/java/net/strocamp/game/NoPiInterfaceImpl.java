package net.strocamp.game;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("testing")
public class NoPiInterfaceImpl implements PiInterface {
    @Override
    public void blink() {

    }

    @Override
    public void ledOn() {

    }

    @Override
    public void ledOff() {

    }

    @Override
    public void onAButton(ButtonEventHandler handler) {

    }

    @Override
    public void onBButton(ButtonEventHandler handler) {

    }

    @Override
    public void onResetButton(ButtonEventHandler handler) {

    }
}
