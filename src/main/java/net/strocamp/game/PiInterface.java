package net.strocamp.game;

public interface PiInterface {
    void blink();

    void ledOn();

    void ledOff();

    void onAButton(ButtonEventHandler handler);

    void onBButton(ButtonEventHandler handler);

    void onResetButton(ButtonEventHandler handler);
}
